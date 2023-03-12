(ns sling-blade-runner.core
  (:gen-class)
  (:require
    [clojure.java.io :as io]
    [clojure.string :as str]))

(defn- read-movies []
  (-> "movies3.txt" io/resource slurp))

(defn- parse-movies [movies]
  (map #(vector % (str/split % #" "))
       (str/split movies #"\n")))

(defn- create-movie [[name parts :as _movie]]
  {:name name, :parts parts})

(defn- create-movies [movies]
  (map create-movie movies))

(defn add-suffix [tree [f & r :as _parts] movie]
  (let [child (when f (add-suffix (get-in tree [:children f]) r movie))]
    (cond-> (or tree {})
            child
            (assoc-in [:children f] child)

            (nil? f)
            (update :movies #(conj (or % []) movie)))))


(defn add-movie [tree {:keys [parts] :as movie}]
  (reduce (fn [acc index] (add-suffix acc (subvec parts index) movie))
          tree
          (range (count parts))))

(defn create-complete-suffix-tree [movies]
  (reduce add-movie nil movies))

(defn get-overlapping-movies
  [tree {:keys [parts] :as movie}]
  (letfn [(search [{:keys [children movies] :as node} [f & r :as _remaining-parts] results]
            (let [new-movies (into results movies)]
              (if (or (some? f) (some? node))
                (search (get children f) r new-movies)
                new-movies)))]

    (remove #(= movie %) (search tree parts []))))

(defn create-graph [tree movies]
  (let [init-graph (->> movies (map #(vector % #{})) (into {}))]
    (reduce
      (fn [graph movie]
        (let [incoming (get-overlapping-movies tree movie)]
          (reduce (fn [graph i] (update graph i conj movie)) graph incoming)))
      init-graph
      movies)))

(defn- get-cycles
  [graph]
  (letfn [(explore [node path-set path {:keys [visited cycles] :as state}]
            (cond
              (path-set node)
              {:visited visited, :cycles (conj cycles [(first path) node])}

              (not (visited node))
              (reduce (fn [state child]
                        (explore child (conj path-set node) (cons node path) state))
                {:visited (conj visited node), :cycles cycles}
                (graph node))

              :else
              state))]
    (->> graph
         keys
         (reduce (fn [state node] (explore node #{} '() state))
                 {:visited #{}, :cycles []})
         :cycles)))


(defn- break-cycles [graph cycles]
  (reduce
    (fn [acc [from to]]
      (update acc from #(disj % to)))
    graph cycles))

(defn- count-edges [graph]
  (->> graph vals (map count) (reduce +)))

(defn- get-post-order [dag]
  (letfn [(explore [{:keys [visited] :as state} node]
            (if (visited node)
              state
              (let [new-state (reduce explore
                                      (update state :visited conj node)
                                      (dag node))]
                (update new-state :order conj node))))]
    (->> dag
         keys
         (reduce explore {:visited #{}, :order []})
         :order)))

(defn- get-distances [dag post-order]
  (reduce
    (fn [distances node]
      (let [children
            (map (juxt identity (comp :distance distances)) (dag node))

            [child child-distance]
            (when (seq children) (apply max-key second children))

            distance
            (if-not (nil? child)
              {:distance (inc child-distance), :child child}
              {:distance 0, :child nil})]

          (assoc distances node distance)))
    {}
    post-order))

(defn- get-longest-path [dag post-order]
  (let [node-distances (get-distances dag post-order)]
    (loop [[node {:keys [_distance child]}]
           (apply max-key (comp :distance val) node-distances)

           result
           []]
      (if (nil? node)
        result
        (recur [child (node-distances child)] (conj result node))))))

(defn- format-oath [path]
  (str/join " " (map #(str "\"" (:name %) "\"") path)))

(defn calculate-longest-title []
  (let [movies        (->> (read-movies) parse-movies create-movies)
        suffix-tree   (create-complete-suffix-tree movies)
        graph         (create-graph suffix-tree movies)
        cycles        (get-cycles graph)
        dag           (break-cycles graph cycles)
        post-order    (get-post-order dag)
        longest-path  (get-longest-path dag post-order)]

    (println "Movies: "         (count movies))
    (println "Vertexes: "       (count graph))
    (println "Edges: "          (count-edges graph))
    (println "Cycles: "         (count cycles))
    (println "DAG edges: "      (count-edges dag))
    (println "Post order: "     (count post-order))
    (println)

    (println (format-oath longest-path))
    (println "Path length: " (count longest-path))))


(defn -main []
  (let [start (System/currentTimeMillis)
        _     (calculate-longest-title)
        end   (System/currentTimeMillis)]
    (println "Time taken: " (- end start) "msecs")))



