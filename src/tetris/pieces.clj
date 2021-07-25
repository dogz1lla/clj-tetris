(ns tetris.pieces)


;; ============================================================================
;; pieces
;; ============================================================================
(defn overlap-ceiling?
  [idx]
  (> 0 idx))

(defn overlap-right-wall?
  [pieces width]
  (not-every? false? (map #(< (first %) width) pieces)))

(defn overlap-left-wall?
  [pieces]
  (not-every? false? (map #(< (first %) 0) pieces)))

(defn overlap? 
  [pieces width]
  (or (overlap-left-wall? pieces)
      (overlap-right-wall? pieces width)))

(defn single-piece
  [x0 y0]
  #{[x0 y0]})

(defn square-piece 
  [x0 y0 width]
  (let [x (loop [i x0] (if (< (inc i) width) i (recur (dec i))))
        p0 [x y0]
        p1 [(inc x) y0]
        p2 [x (inc y0)]
        p3 [(inc x) (inc y0)]
        square #{p0 p1 p2 p3}]
    square))

(defn gamma-piece 
  [x0 y0 width]
  (let [x (loop [i x0] (if (< (+ i 2) width) i (recur (dec i))))
        p0 [x y0]
        p1 [(inc x) y0]
        p2 [(+ x 2) y0]
        p3 [x (inc y0)]
        gamma #{p0 p1 p2 p3}] 
    gamma))

(defn gamma-piece-mirror
  [x0 y0 width]
  (let [x (loop [i x0] (if (< (+ i 2) width) i (recur (dec i))))
        p0 [x y0]
        p1 [(inc x) y0]
        p2 [(+ x 2) y0]
        p3 [(+ x 2) (inc y0)]
        gamma #{p0 p1 p2 p3}] 
    gamma))

(defn tau-piece
  [x0 y0 width]
  (let [x (loop [i x0] (if (< (+ i 2) width) i (recur (dec i))))
        p0 [x y0]
        p1 [(inc x) y0]
        p2 [(+ x 2) y0]
        p3 [(inc x) (inc y0)]
        tau #{p0 p1 p2 p3}] 
    tau))

(defn sausage-piece
  [x0 y0 width]
  (let [x (loop [i x0] (if (< (+ i 3) width) i (recur (dec i))))
        p0 [x y0]
        p1 [(inc x) y0]
        p2 [(+ x 2) y0]
        p3 [(+ x 3) y0]
        sausage #{p0 p1 p2 p3}] 
    sausage))

(defn step-piece
  [x0 y0 width]
  (let [x (loop [i x0] (if (< (+ i 2) width) i (recur (dec i))))
        p0 [x y0]
        p1 [(inc x) y0]
        p2 [(inc x) (inc y0)]
        p3 [(+ x 2) (inc y0)]
        step #{p0 p1 p2 p3}] 
    step))

(defn step-piece-mirror
  [x0 y0 width]
  (let [xl (loop [i x0] (if (>= (dec i) 0) i (recur (inc i))))
        xr (loop [i xl] (if (< (+ i 2) width) i (recur (dec i))))
        p0 [xr y0]
        p1 [(inc xr) y0]
        p2 [(inc xr) (inc y0)]
        p3 [(+ xr 2) (inc y0)]
        step #{p0 p1 p2 p3}] 
    step))
