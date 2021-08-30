(ns tetris.linalg)


;; ----------------------------------------------------------
;; linear algebra
;; ----------------------------------------------------------
(def no-turn [[1 0] [0 1]])
(def full-turn [[-1 0] [0 -1]])
(def clockwise-turn [[0 1] [-1 0]])
(def counterclockwise-turn [[0 -1] [1 0]])

(defn scale
  [c v]
  (mapv #(* c %) v))

(defn vec+
  [v1 v2]
  (mapv + v1 v2))

(defn lin-combination
  ([v1 v2]
   (vec+ v1 v2))
  ([v1 v2 c1 c2]
   (vec+ (scale c1 v1) (scale c2 v2))))

(defn scalar-product
  [v w]
  (reduce + (map * v w)))

(defn lin-transform
  [M x]
  (mapv #(scalar-product % x) M))

(defn vect=
  [v1 v2]
  {:pre [(= (count v1) (count v2))]}
  (= v1 v2))

(defn rotation-matrix
  [phi]
  [[(Math/cos phi) (- (Math/sin phi))] [(Math/sin phi) (Math/cos phi)]])

(def R-pi [[0 -1] [1 0]])
