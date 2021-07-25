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
  (let [p0 [x0 y0]
        p1 [(inc x0) y0]
        p2 [x0 (inc y0)]
        p3 [(inc x0) (inc y0)]
        square #{p0 p1 p2 p3}]
    square))

(defn gamma-piece 
  [x0 y0 width]
  (let [p0 [x0 y0]
        p1 [(inc x0) y0]
        p2 [(+ x0 2) y0]
        p3 [x0 (inc y0)]] 
    #{p0 p1 p2 p3}))

(defn gamma-piece-mirror
  [p0 width]
  (let [p1 (inc p0)
        p2 (inc p1)
        p3 (+ width p2)] 
    [p0 #{p1 p2 p3}]))

(defn tau-piece 
  [p0 width]
  (let [p1 (inc p0)
        p2 (inc p1)
        p3 (+ width p1)] 
    [p0 #{p1 p2 p3}]))

(defn sausage-piece 
  [p0]
  (let [p1 (inc p0)
        p2 (inc p1)
        p3 (inc p2)] 
    [p0 #{p1 p2 p3}]))

(defn step-piece 
  [p0 width]
  (let [p1 (inc p0)
        p2 (+ width p1)
        p3 (inc p2)] 
    [p0 #{p1 p2 p3}]))

(defn step-piece-mirror
  [p0 width]
  (let [p1 (inc p0)
        p2 (+ width p0)
        p3 (dec p2)] 
    [p0 #{p1 p2 p3}]))

#_(defn test-run
  [width]
  ;;(reset! game-history [(square-piece 0)]))
  ;;(reset! game-history [(gamma-piece 0)]))
  ;;(reset! game-history [(tau-piece 0)]))
  ;;(reset! game-history [(gamma-piece-mirror 0)]))
  ;;(reset! game-history [(sausage-piece 0)]))
  ;;(reset! game-history [(step-piece 0)]))
  (reset! game-history [(step-piece-mirror 1 width)]))
