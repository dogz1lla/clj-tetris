(ns tetris.pieces)


;; ============================================================================
;; pieces
;; ============================================================================
(defn overlap-ceiling?
  [idx]
  (> 0 idx))

(defn overlap-right-wall?
  [idx width]
  (>= idx width))

(defn overlap-left-wall?
  [idx]
  (> 0 idx))

(defn overlap? 
  [piece width]
  not-every? false? (map #(<= (- max-idx %) width) piece))

(defn single-piece
  [p0]
  #{p0})

(defn square-piece 
  [p0 width]
  (let [p1 (inc p0)
        p2 (+ width p0)
        p3 (+ width p1)] 
    [p0 #{p1 p2 p3}]))

(defn gamma-piece 
  [p0 width]
  (let [p1 (inc p0)
        p2 (inc p1)
        p3 (+ width p0)] 
    [p0 #{p1 p2 p3}]))

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
