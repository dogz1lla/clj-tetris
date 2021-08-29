(ns tetris.pieces
  (:require [tetris.linalg :as la]
            [tetris.utils :as u]))


;; ;; ============================================================================
;; ;; Piece protocol
;; ;; ============================================================================
;; (defprotocol Piece
;;   "A tetris piece protocol."
;;   (spawn [this x0 y0] 
;;     "Spawn a piece. Replacement for a constructor.")
;;   (check-wall-collision [this width]
;;     "Check if there is a collision with a wall.")
;;   (shift [this dx width]
;;     "Translate the piece horizontally by dx.")
;;   (rotate [this]
;;     "Rotate the piece by pi/2 clockwise.")
;;   (fall [this]
;;     "Descend the piece by one unit."))
;; 
;; ;; ============================================================================
;; ;; Pieces implementation
;; ;; ============================================================================
;; (defrecord SquarePiece [p0 p1 p2 p3]
;;   Piece
;; 
;;   (spawn [_ x0 y0]
;;     (let [p0 [x0 y0]
;;           p1 [(inc x0) y0]
;;           p2 [x0 (inc y0)]
;;           p3 [(inc x0) (inc y0)]]
;;       (SquarePiece. p0 p1 p2 p3)))
;; 
;;   (check-wall-collision [this width]
;;     (let [[x0 y0] (:p0 this)
;;           new-x0 (loop [i x0] 
;;                    (if (< (inc i) width) i (recur (dec i))))
;;           new-x0 (if (< new-x0 0) 0 new-x0)]
;;       (spawn this new-x0 y0)))
;; 
;;   (fall [this] 
;;     (let [[x0 y0] (:p0 this)]
;;       (spawn this x0 (inc y0))))
;; 
;;   (rotate [this] 
;;     this)
;; 
;;   (shift [this dx width]
;;     (let [[x0 y0] (:p0 this)
;;           new-x0 (+ dx x0)
;;           shifted (spawn this new-x0 y0)]
;;       (check-wall-collision shifted width))))
;; 
;; (defrecord GammaPiece [p0 p1 p2 p3]
;;   Piece
;; 
;;   (spawn [_ x0 y0]
;;     (let [p0 [x0 y0]
;;           p1 [(inc x0) y0]
;;           p2 [(+ x0 2) y0]
;;           p3 [x0 (inc y0)]]
;;       (GammaPiece. p0 p1 p2 p3)))
;; 
;;   (check-wall-collision [this width]
;;     (let [[x0 y0] (:p0 this)
;;           new-x0 (loop [i x0] (if (< (+ i 2) width) i (recur (dec i))))
;;           new-x0 (if (< new-x0 0) 0 new-x0)]
;;       (spawn this new-x0 y0)))
;; 
;;   (fall [this] 
;;     (let [[x0 y0] (:p0 this)]
;;       (spawn this x0 (inc y0))))
;; 
;;   (rotate [this] 
;;     (let [{:keys [p0 p1 p2 p3]} this
;;           shift [(inc (first p0)) (inc (last p0))]
;;           ;translated (map #([(dec (first %)) (dec (last %))]) [p0 p1 p2 p3])
;;           ;_ (println [p0 p1 p2 p3])
;;           translated (reduce #(conj %1 [(- (first %2) (first shift)) (- (last %2) (last shift))]) [] [p0 p1 p2 p3])
;;           ;_ (println translated)
;;           ;rotated (map #([(- (last %)) (first %)]) translated)
;;           rotated (reduce #(conj %1 [(- (last %2)) (first %2)]) [] translated)
;;           ;_ (println rotated)
;;           ;[p0 p1 p2 p3] (mapv #([(inc (first %)) (inc (last %))]) rotated)
;;           [p0 p1 p2 p3] (reduce #(conj %1 [(+ (first %2) (first shift)) (+ (last %2) (last shift))]) [] rotated)
;;           ;_ (println [p0 p1 p2 p3])
;;           ]
;;       (assoc this :p0 p0 :p1 p1 :p2 p2 :p3 p3)))
;; 
;;   (shift [this dx width]
;;     (let [[x0 y0] (:p0 this)
;;           new-x0 (+ dx x0)
;;           shifted (spawn this new-x0 y0)]
;;       (check-wall-collision shifted width))))
;; 
;; (defrecord GammaPieceMirror [p0 p1 p2 p3]
;;   Piece
;; 
;;   (spawn [_ x0 y0]
;;     (let [p0 [x0 y0]
;;           p1 [(inc x0) y0]
;;           p2 [(+ x0 2) y0]
;;           p3 [(+ x0 2) (inc y0)]]
;;       (GammaPieceMirror. p0 p1 p2 p3)))
;; 
;;   (check-wall-collision [this width]
;;     (let [[x0 y0] (:p0 this)
;;           new-x0 (loop [i x0] (if (< (+ i 2) width) i (recur (dec i))))
;;           new-x0 (if (< new-x0 0) 0 new-x0)]
;;       (spawn this new-x0 y0)))
;; 
;;   (fall [this] 
;;     (let [[x0 y0] (:p0 this)]
;;       (spawn this x0 (inc y0))))
;; 
;;   (shift [this dx width]
;;     (let [[x0 y0] (:p0 this)
;;           new-x0 (+ dx x0)
;;           shifted (spawn this new-x0 y0)]
;;       (check-wall-collision shifted width))))
;; 
;; 
;; (defrecord TauPiece [p0 p1 p2 p3]
;;   Piece
;; 
;;   (spawn [_ x0 y0]
;;     (let [p0 [x0 y0]
;;           p1 [(inc x0) y0]
;;           p2 [(+ x0 2) y0]
;;           p3 [(inc x0) (inc y0)]]
;;       (TauPiece. p0 p1 p2 p3)))
;; 
;;   (check-wall-collision [this width]
;;     (let [[x0 y0] (:p0 this)
;;           new-x0 (loop [i x0] (if (< (+ i 2) width) i (recur (dec i))))
;;           new-x0 (if (< new-x0 0) 0 new-x0)]
;;       (spawn this new-x0 y0)))
;; 
;;   (fall [this] 
;;     (let [[x0 y0] (:p0 this)]
;;       (spawn this x0 (inc y0))))
;; 
;;   (shift [this dx width]
;;     (let [[x0 y0] (:p0 this)
;;           new-x0 (+ dx x0)
;;           shifted (spawn this new-x0 y0)]
;;       (check-wall-collision shifted width))))
;; 
;; 
;; (defrecord SausagePiece [p0 p1 p2 p3]
;;   Piece
;; 
;;   (spawn [_ x0 y0]
;;     (let [p0 [x0 y0]
;;           p1 [(inc x0) y0]
;;           p2 [(+ x0 2) y0]
;;           p3 [(+ x0 3) y0]]
;;       (SausagePiece. p0 p1 p2 p3)))
;; 
;;   (check-wall-collision [this width]
;;     (let [[x0 y0] (:p0 this)
;;           new-x0 (loop [i x0] (if (< (+ i 3) width) i (recur (dec i))))
;;           new-x0 (if (< new-x0 0) 0 new-x0)]
;;       (spawn this new-x0 y0)))
;; 
;;   (fall [this] 
;;     (let [[x0 y0] (:p0 this)]
;;       (spawn this x0 (inc y0))))
;; 
;;   (shift [this dx width]
;;     (let [[x0 y0] (:p0 this)
;;           new-x0 (+ dx x0)
;;           shifted (spawn this new-x0 y0)]
;;       (check-wall-collision shifted width))))
;; 
;; 
;; (defrecord StepPiece [p0 p1 p2 p3]
;;   Piece
;; 
;;   (spawn [_ x0 y0]
;;     (let [p0 [x0 y0]
;;           p1 [(inc x0) y0]
;;           p2 [(inc x0) (inc y0)]
;;           p3 [(+ x0 2) (inc y0)]]
;;       (StepPiece. p0 p1 p2 p3)))
;; 
;;   (check-wall-collision [this width]
;;     (let [[x0 y0] (:p0 this)
;;           new-x0 (loop [i x0] (if (< (+ i 2) width) i (recur (dec i))))
;;           new-x0 (if (< new-x0 0) 0 new-x0)]
;;       (spawn this new-x0 y0)))
;; 
;;   (fall [this] 
;;     (let [[x0 y0] (:p0 this)]
;;       (spawn this x0 (inc y0))))
;; 
;;   (shift [this dx width]
;;     (let [[x0 y0] (:p0 this)
;;           new-x0 (+ dx x0)
;;           shifted (spawn this new-x0 y0)]
;;       (check-wall-collision shifted width))))
;; 
;; 
;; (defrecord StepPieceMirror [p0 p1 p2 p3]
;;   Piece
;; 
;;   (spawn [_ x0 y0]
;;     (let [p0 [x0 y0]
;;           p1 [(inc x0) y0]
;;           p2 [(dec x0) (inc y0)]
;;           p3 [x0 (inc y0)]]
;;       (StepPieceMirror. p0 p1 p2 p3)))
;; 
;;   (check-wall-collision [this width]
;;     (let [[x0 y0] (:p0 this)
;;           new-x0 (loop [i x0] (if (< (inc i) width) i (recur (dec i))))
;;           new-x0 (loop [i new-x0] (if (>= (dec i) 0) i (recur (inc i))))]
;;       (spawn this new-x0 y0)))
;; 
;;   (fall [this] 
;;     (let [[x0 y0] (:p0 this)]
;;       (spawn this x0 (inc y0))))
;; 
;;   (shift [this dx width]
;;     (let [[x0 y0] (:p0 this)
;;           new-x0 (+ dx x0)
;;           shifted (spawn this new-x0 y0)]
;;       (check-wall-collision shifted width))))

;; 
;; ;; ============================================================================
;; ;; Piece initialization
;; ;; ============================================================================
;; (defn square-piece [x0 y0 width]
;;   (-> (SquarePiece. nil nil nil nil)
;;       (spawn x0 y0)
;;       (check-wall-collision width)))
;; 
;; (defn gamma-piece [x0 y0 width]
;;   (-> (GammaPiece. nil nil nil nil)
;;       (spawn x0 y0)
;;       (check-wall-collision width)))
;; 
;; (defn gamma-piece-mirror [x0 y0 width]
;;   (-> (GammaPieceMirror. nil nil nil nil)
;;       (spawn x0 y0)
;;       (check-wall-collision width)))
;; 
;; (defn tau-piece [x0 y0 width]
;;   (-> (TauPiece. nil nil nil nil)
;;       (spawn x0 y0)
;;       (check-wall-collision width)))
;; 
;; (defn sausage-piece [x0 y0 width]
;;   (-> (SausagePiece. nil nil nil nil)
;;       (spawn x0 y0)
;;       (check-wall-collision width)))
;; 
;; (defn step-piece [x0 y0 width]
;;   (-> (StepPiece. nil nil nil nil)
;;       (spawn x0 y0)
;;       (check-wall-collision width)))
;; 
;; (defn step-piece-mirror [x0 y0 width]
;;   (-> (StepPieceMirror. nil nil nil nil)
;;       (spawn x0 y0)
;;       (check-wall-collision width)))
;; 

;; ============================================================================
;; Piece protocol
;; ============================================================================
(defprotocol Piece
  "A tetris piece protocol."
  (parts [this] 
    "Get a list of sub-pieces.")
  (shift [this dx]
    "Translate the piece horizontally by dx.")
  (fall [this dy]
    "Descend the piece by one unit.")
  (check-right-wall-collision [this width]
    "Check if there is a collision with a right wall.")
  (check-left-wall-collision [this]
    "Check if there is a collision with a left wall.")
  (rotate [this]
    "Rotate the piece by pi/2 clockwise.")
  (compensate-rotation [this position orientation]
    "To not shift the piece after rotation we need to compensate for it."))


(defrecord GammaPiece [position orientation]
  Piece

  (parts [this]
    (let [{:keys [position orientation]} this
          p0 position
          p1 (la/vec+ p0 orientation)
          p2 (la/vec+ p1 orientation)
          R (la/rotation-matrix (* 0.5 Math/PI))
          orth-orientation (la/lin-transform R orientation)
          ;; NOTE need to round elements of the orientation vector because
          ;; coord has to have integer elements
          orth-orientation-int [(Math/round (first orth-orientation)) 
                                (Math/round (last orth-orientation))]
          p3 (la/vec+ position orth-orientation-int)]
      [p0 p1 p2 p3]))

  (compensate-rotation [_ position orientation]
    (case orientation
      [ 1  0] (la/vec+ position [ 0 -2])
      [ 0  1] (la/vec+ position [ 1  0])
      [-1  0] (la/vec+ position [ 1  1])
      [ 0 -1] (la/vec+ position [-2  1])))

  (shift [this dx]
    (let [{:keys [orientation position]} this
          new-position (la/vec+ position (la/scale dx [1 0]))]
      (->GammaPiece new-position orientation)))

  (fall [this dy] 
    (let [{:keys [orientation position]} this
          new-position (la/vec+ position (la/scale dy [0 1]))]
      (->GammaPiece new-position orientation)))

  (check-left-wall-collision [this]
    (loop [current-piece this]
      (let [piece-parts (parts current-piece)
            leftmost (u/leftmost piece-parts)]
        (if (>= leftmost 0)
          current-piece
          (recur (shift current-piece 1))))))

  (check-right-wall-collision [this width]
      (loop [current-piece this]
        (let [piece-parts (parts current-piece)
              rightmost (u/rightmost piece-parts)]
          (if (< rightmost width)
            current-piece
            (recur (shift current-piece -1))))))

  (rotate [this]
    (let [{:keys [position orientation]} this
          R (la/rotation-matrix (* 0.5 Math/PI))
          new-orientation (la/lin-transform R orientation)
          ;; NOTE need to round elements of the orientation vector because
          ;; coord has to have integer elements
          new-orientation-int [(Math/round (first new-orientation)) 
                               (Math/round (last new-orientation))]
          new-position (compensate-rotation this position new-orientation-int)]
      (->GammaPiece new-position new-orientation-int))))

(defrecord SquarePiece [position orientation]
  Piece

  (parts [this]
    (let [{:keys [position]} this
          p0 position
          p1 (la/vec+ p0 [1 0])
          p2 (la/vec+ p1 [0 1])
          p3 (la/vec+ p2 [-1 0])]
      [p0 p1 p2 p3]))

  (compensate-rotation [_ position _]
    position)

  (shift [this dx]
    (let [{:keys [orientation position]} this
          new-position (la/vec+ position (la/scale dx [1 0]))]
      (->SquarePiece new-position orientation)))

  (fall [this dy] 
    (let [{:keys [orientation position]} this
          new-position (la/vec+ position (la/scale dy [0 1]))]
      (->SquarePiece new-position orientation)))

  (check-left-wall-collision [this]
    (loop [current-piece this]
      (let [piece-parts (parts current-piece)
            leftmost (u/leftmost piece-parts)]
        (if (>= leftmost 0)
          current-piece
          (recur (shift current-piece 1))))))

  (check-right-wall-collision [this width]
    (loop [current-piece this]
      (let [piece-parts (parts current-piece)
            rightmost (u/rightmost piece-parts)]
        (if (< rightmost width)
          current-piece
          (recur (shift current-piece -1))))))

  (rotate [this]
    this))

(defrecord GammaPieceMirror [position orientation]
  Piece

  (parts [this]
    (let [{:keys [position orientation]} this
          p0 position
          p1 (la/vec+ p0 orientation)
          p2 (la/vec+ p1 orientation)
          R (la/rotation-matrix (* 0.5 Math/PI))
          orth-orientation (la/lin-transform R orientation)
          ;; NOTE need to round elements of the orientation vector because
          ;; coord has to have integer elements
          orth-orientation-int [(Math/round (first orth-orientation)) 
                                (Math/round (last orth-orientation))]
          p3 (la/vec+ p2 orth-orientation-int)]
      [p0 p1 p2 p3]))

  (compensate-rotation [_ position orientation]
    (case orientation
      [ 1  0] (la/vec+ position [ 0 -2])
      [ 0  1] (la/vec+ position [ 1  0])
      [-1  0] (la/vec+ position [ 1  1])
      [ 0 -1] (la/vec+ position [-2  1])))

  (shift [this dx]
    (let [{:keys [orientation position]} this
          new-position (la/vec+ position (la/scale dx [1 0]))]
      (->GammaPieceMirror new-position orientation)))

  (fall [this dy] 
    (let [{:keys [orientation position]} this
          new-position (la/vec+ position (la/scale dy [0 1]))]
      (->GammaPieceMirror new-position orientation)))

  (check-left-wall-collision [this]
    (loop [current-piece this]
      (let [piece-parts (parts current-piece)
            leftmost (u/leftmost piece-parts)]
        (if (>= leftmost 0)
          current-piece
          (recur (shift current-piece 1))))))

  (check-right-wall-collision [this width]
    (loop [current-piece this]
      (let [piece-parts (parts current-piece)
            rightmost (u/rightmost piece-parts)]
        (if (< rightmost width)
          current-piece
          (recur (shift current-piece -1))))))

  (rotate [this]
    (let [{:keys [position orientation]} this
          R (la/rotation-matrix (* 0.5 Math/PI))
          new-orientation (la/lin-transform R orientation)
          ;; NOTE need to round elements of the orientation vector because
          ;; coord has to have integer elements
          new-orientation-int [(Math/round (first new-orientation)) 
                               (Math/round (last new-orientation))]
          new-position (compensate-rotation this position new-orientation-int)]
      (->GammaPieceMirror new-position new-orientation-int))))

(defrecord SausagePiece [position orientation]
  Piece

  (parts [this]
    (let [{:keys [position orientation]} this
          p0 position
          p1 (la/vec+ p0 orientation)
          p2 (la/vec+ p1 orientation)
          p3 (la/vec+ p2 orientation)]
      [p0 p1 p2 p3]))

  (compensate-rotation [_ position orientation]
    (case orientation
      [ 1  0] (la/vec+ position [ 0 -3])
      [ 0  1] position 
      [-1  0] (la/vec+ position [ 3  0])
      [ 0 -1] (la/vec+ position [-3  3])))

  (shift [this dx]
    (let [{:keys [orientation position]} this
          new-position (la/vec+ position (la/scale dx [1 0]))]
      (->SausagePiece new-position orientation)))

  (fall [this dy] 
    (let [{:keys [orientation position]} this
          new-position (la/vec+ position (la/scale dy [0 1]))]
      (->SausagePiece new-position orientation)))

  (check-left-wall-collision [this]
    (loop [current-piece this]
      (let [piece-parts (parts current-piece)
            leftmost (u/leftmost piece-parts)]
        (if (>= leftmost 0)
          current-piece
          (recur (shift current-piece 1))))))

  (check-right-wall-collision [this width]
    (loop [current-piece this]
      (let [piece-parts (parts current-piece)
            rightmost (u/rightmost piece-parts)]
        (if (< rightmost width)
          current-piece
          (recur (shift current-piece -1))))))

  (rotate [this]
    (let [{:keys [position orientation]} this
          R (la/rotation-matrix (* 0.5 Math/PI))
          new-orientation (la/lin-transform R orientation)
          ;; NOTE need to round elements of the orientation vector because
          ;; coord has to have integer elements
          new-orientation-int [(Math/round (first new-orientation)) 
                               (Math/round (last new-orientation))]
          new-position (compensate-rotation this position new-orientation-int)]
      (->SausagePiece new-position new-orientation-int))))

(defn gamma-piece [x0 y0 width]
  (check-right-wall-collision (GammaPiece. [x0 y0] [1 0]) width))

(defn square-piece [x0 y0 width]
  (check-right-wall-collision (SquarePiece. [x0 y0] [1 0]) width))

(defn gamma-piece-mirror [x0 y0 width]
  (check-right-wall-collision (GammaPieceMirror. [x0 y0] [1 0]) width))

(defn sausage-piece [x0 y0 width]
  (check-right-wall-collision (SausagePiece. [x0 y0] [1 0]) width))
