(ns tetris.pieces
  (:require [tetris.linalg :as la]
            [tetris.utils :as u]))


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


;; ============================================================================
;; Pieces implementation
;; ============================================================================
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

(defrecord StepPiece [position orientation]
  Piece

  (parts [this]
    (let [{:keys [position orientation]} this
          p0 position
          p1 (la/vec+ p0 orientation)
          R (la/rotation-matrix (* 0.5 Math/PI))
          orth-orientation (la/lin-transform R orientation)
          ;; NOTE need to round elements of the orientation vector because
          ;; coord has to have integer elements
          orth-orientation-int [(Math/round (first orth-orientation)) 
                                (Math/round (last orth-orientation))]
          p2 (la/vec+ p0 orth-orientation-int)
          p3 (la/vec+ p2 (la/scale -1 orientation))]
      [p0 p1 p2 p3]))

  (compensate-rotation [_ position orientation]
    (case orientation
      [ 1  0] (la/vec+ position [ 0 -1])
      [ 0  1] (la/vec+ position [ 1  1])
      [-1  0] (la/vec+ position [-1  0])
      [ 0 -1] (la/vec+ position [ 0  0])))

  (shift [this dx]
    (let [{:keys [orientation position]} this
          new-position (la/vec+ position (la/scale dx [1 0]))]
      (->StepPiece new-position orientation)))

  (fall [this dy] 
    (let [{:keys [orientation position]} this
          new-position (la/vec+ position (la/scale dy [0 1]))]
      (->StepPiece new-position orientation)))

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
      (->StepPiece new-position new-orientation-int))))

(defrecord StepPieceMirror [position orientation]
  Piece

  (parts [this]
    (let [{:keys [position orientation]} this
          p0 position
          p1 (la/vec+ p0 orientation)
          R (la/rotation-matrix (* 0.5 Math/PI))
          orth-orientation (la/lin-transform R orientation)
          ;; NOTE need to round elements of the orientation vector because
          ;; coord has to have integer elements
          orth-orientation-int [(Math/round (first orth-orientation)) 
                                (Math/round (last orth-orientation))]
          p2 (la/vec+ p1 orth-orientation-int)
          p3 (la/vec+ p2 orientation)]
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
      (->StepPieceMirror new-position orientation)))

  (fall [this dy] 
    (let [{:keys [orientation position]} this
          new-position (la/vec+ position (la/scale dy [0 1]))]
      (->StepPieceMirror new-position orientation)))

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
      (->StepPieceMirror new-position new-orientation-int))))

(defrecord TauPiece [position orientation]
  Piece

  (parts [this]
    (let [{:keys [position orientation]} this
          p0 position
          p1 (la/vec+ p0 orientation)
          p2 (la/vec+ p0 (la/scale -1 orientation))
          R (la/rotation-matrix (* 0.5 Math/PI))
          orth-orientation (la/lin-transform R orientation)
          ;; NOTE need to round elements of the orientation vector because
          ;; coord has to have integer elements
          orth-orientation-int [(Math/round (first orth-orientation)) 
                                (Math/round (last orth-orientation))]
          p3 (la/vec+ p0 orth-orientation-int)]
      [p0 p1 p2 p3]))

  (compensate-rotation [_ position orientation]
    (case orientation
      [ 1  0] (la/vec+ position [ 0 -1])
      [ 0  1] (la/vec+ position [ 0  1])
      [-1  0] (la/vec+ position [ 0  0])
      [ 0 -1] (la/vec+ position [ 0  0])))

  (shift [this dx]
    (let [{:keys [orientation position]} this
          new-position (la/vec+ position (la/scale dx [1 0]))]
      (->TauPiece new-position orientation)))

  (fall [this dy] 
    (let [{:keys [orientation position]} this
          new-position (la/vec+ position (la/scale dy [0 1]))]
      (->TauPiece new-position orientation)))

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
      (->TauPiece new-position new-orientation-int))))


;; ============================================================================
;; piece constructors
;; ============================================================================
(defn gamma-piece [x0 y0 width]
  (check-right-wall-collision (->GammaPiece [x0 y0] [1 0]) width))

(defn square-piece [x0 y0 width]
  (check-right-wall-collision (->SquarePiece [x0 y0] [1 0]) width))

(defn gamma-piece-mirror [x0 y0 width]
  (check-right-wall-collision (->GammaPieceMirror [x0 y0] [1 0]) width))

(defn sausage-piece [x0 y0 width]
  (check-right-wall-collision (->SausagePiece [x0 y0] [1 0]) width))

(defn step-piece [x0 y0 width]
  (check-right-wall-collision (->StepPiece [x0 y0] [1 0]) width))

(defn step-piece-mirror [x0 y0 width]
  (check-right-wall-collision (->StepPieceMirror [x0 y0] [1 0]) width))

(defn tau-piece [x0 y0 width]
  (check-right-wall-collision (->TauPiece [x0 y0] [1 0]) width))
