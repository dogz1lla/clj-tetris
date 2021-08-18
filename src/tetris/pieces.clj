(ns tetris.pieces)


;; ============================================================================
;; Piece protocol
;; ============================================================================
(defprotocol Piece
  "A tetris piece protocol."
  (spawn [this x0 y0] 
    "Spawn a piece. Replacement for a constructor.")
  (check-collisions [this width]
    "Check if there is a collision with a wall.")
  (shift [this dx width]
    "Translate the piece horizontally by dx."))

;; ============================================================================
;; Pieces implementation
;; ============================================================================
(defrecord SquarePiece [p0 p1 p2 p3]
  Piece
  (spawn [this x0 y0]
    (let [p0 [x0 y0]
          p1 [(inc x0) y0]
          p2 [x0 (inc y0)]
          p3 [(inc x0) (inc y0)]]
      (SquarePiece. p0 p1 p2 p3)))
  (check-collisions [this width]
    (let [[x0 y0] (:p0 this)
          new-x0 (loop [i x0] 
                   (if (< (inc i) width) i (recur (dec i))))
          new-x0 (if (< new-x0 0) 0 new-x0)]
      (spawn this new-x0 y0)))
  (shift [this dx width]
    (let [[x0 y0] (:p0 this)
          new-x0 (+ dx x0)
          shifted (spawn this new-x0 y0)]
      (check-collisions shifted width))))

(defrecord GammaPiece [p0 p1 p2 p3]
  Piece
  (spawn [this x0 y0]
    (let [p0 [x0 y0]
          p1 [(inc x0) y0]
          p2 [(+ x0 2) y0]
          p3 [x0 (inc y0)]]
      (GammaPiece. p0 p1 p2 p3)))
  (check-collisions [this width]
    (let [[x0 y0] (:p0 this)
          new-x0 (loop [i x0] (if (< (+ i 2) width) i (recur (dec i))))
          new-x0 (if (< new-x0 0) 0 new-x0)]
      (spawn this new-x0 y0)))
  (shift [this dx width]
    (let [[x0 y0] (:p0 this)
          new-x0 (+ dx x0)
          shifted (spawn this new-x0 y0)]
      (check-collisions shifted width))))

(defrecord GammaPieceMirror [p0 p1 p2 p3]
  Piece
  (spawn [this x0 y0]
    (let [p0 [x0 y0]
          p1 [(inc x0) y0]
          p2 [(+ x0 2) y0]
          p3 [(+ x0 2) (inc y0)]]
      (GammaPieceMirror. p0 p1 p2 p3)))
  (check-collisions [this width]
    (let [[x0 y0] (:p0 this)
          new-x0 (loop [i x0] (if (< (+ i 2) width) i (recur (dec i))))
          new-x0 (if (< new-x0 0) 0 new-x0)]
      (spawn this new-x0 y0)))
  (shift [this dx width]
    (let [[x0 y0] (:p0 this)
          new-x0 (+ dx x0)
          shifted (spawn this new-x0 y0)]
      (check-collisions shifted width))))

(defrecord TauPiece [p0 p1 p2 p3]
  Piece
  (spawn [this x0 y0]
    (let [p0 [x0 y0]
          p1 [(inc x0) y0]
          p2 [(+ x0 2) y0]
          p3 [(inc x0) (inc y0)]]
      (TauPiece. p0 p1 p2 p3)))
  (check-collisions [this width]
    (let [[x0 y0] (:p0 this)
          new-x0 (loop [i x0] (if (< (+ i 2) width) i (recur (dec i))))
          new-x0 (if (< new-x0 0) 0 new-x0)]
      (spawn this new-x0 y0)))
  (shift [this dx width]
    (let [[x0 y0] (:p0 this)
          new-x0 (+ dx x0)
          shifted (spawn this new-x0 y0)]
      (check-collisions shifted width))))

(defrecord SausagePiece [p0 p1 p2 p3]
  Piece
  (spawn [this x0 y0]
    (let [p0 [x0 y0]
          p1 [(inc x0) y0]
          p2 [(+ x0 2) y0]
          p3 [(+ x0 3) y0]]
      (SausagePiece. p0 p1 p2 p3)))
  (check-collisions [this width]
    (let [[x0 y0] (:p0 this)
          new-x0 (loop [i x0] (if (< (+ i 3) width) i (recur (dec i))))
          new-x0 (if (< new-x0 0) 0 new-x0)]
      (spawn this new-x0 y0)))
  (shift [this dx width]
    (let [[x0 y0] (:p0 this)
          new-x0 (+ dx x0)
          shifted (spawn this new-x0 y0)]
      (check-collisions shifted width))))

(defrecord StepPiece [p0 p1 p2 p3]
  Piece
  (spawn [this x0 y0]
    (let [p0 [x0 y0]
          p1 [(inc x0) y0]
          p2 [(inc x0) (inc y0)]
          p3 [(+ x0 2) (inc y0)]]
      (StepPiece. p0 p1 p2 p3)))
  (check-collisions [this width]
    (let [[x0 y0] (:p0 this)
          new-x0 (loop [i x0] (if (< (+ i 2) width) i (recur (dec i))))
          new-x0 (if (< new-x0 0) 0 new-x0)]
      (spawn this new-x0 y0)))
  (shift [this dx width]
    (let [[x0 y0] (:p0 this)
          new-x0 (+ dx x0)
          shifted (spawn this new-x0 y0)]
      (check-collisions shifted width))))

(defrecord StepPieceMirror [p0 p1 p2 p3]
  Piece
  (spawn [this x0 y0]
    (let [p0 [x0 y0]
          p1 [(inc x0) y0]
          p2 [(inc x0) (inc y0)]
          p3 [(+ x0 2) (inc y0)]]
      (StepPieceMirror. p0 p1 p2 p3)))
  (check-collisions [this width]
    (let [[x0 y0] (:p0 this)
          new-x0 (loop [i x0] (if (< (+ i 2) width) i (recur (dec i))))
          new-x0 (loop [i new-x0] (if (>= (dec i) 0) i (recur (inc i))))]
      (spawn this new-x0 y0)))
  (shift [this dx width]
    (let [[x0 y0] (:p0 this)
          new-x0 (+ dx x0)
          shifted (spawn this new-x0 y0)]
      (check-collisions shifted width))))

;; ============================================================================
;; Piece initialization
;; ============================================================================
(defn square-piece [x0 y0 width]
  (-> (SquarePiece. nil nil nil nil)
      (spawn x0 y0)
      (check-collisions width)))

(defn gamma-piece [x0 y0 width]
  (-> (GammaPiece. nil nil nil nil)
      (spawn x0 y0)
      (check-collisions width)))

(defn gamma-piece-mirror [x0 y0 width]
  (-> (GammaPieceMirror. nil nil nil nil)
      (spawn x0 y0)
      (check-collisions width)))

(defn tau-piece [x0 y0 width]
  (-> (TauPiece. nil nil nil nil)
      (spawn x0 y0)
      (check-collisions width)))

(defn sausage-piece [x0 y0 width]
  (-> (SausagePiece. nil nil nil nil)
      (spawn x0 y0)
      (check-collisions width)))

(defn step-piece [x0 y0 width]
  (-> (StepPiece. nil nil nil nil)
      (spawn x0 y0)
      (check-collisions width)))

(defn step-piece-mirror [x0 y0 width]
  (-> (StepPieceMirror. nil nil nil nil)
      (spawn x0 y0)
      (check-collisions width)))

;; Need to have an equivalent of a Piece class; constructor should ideally only
;; take (x0,y0) and create the rest of the coordinates; it should also 
;; definitely check for the wall overlaps and shift the (x0, y0) accordingly;
;; the method the class should implement is shift given dx 

;(defprotocol TestProtocol
;  (method-1 [x])
;  (method-2 [x]))
;
;(defrecord TestRecord [x]
;  TestProtocol
;  (method-1 [x] "hi from method 1!")
;  (method-2 [x] (str "method 1 says: " (method-1 x))))
