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
  (compensate-rotation [this position orientation]
    "To not shift the piece after rotation we need to compensate for it."))

;; ============================================================================
;; Methods common to all pieces
;; ============================================================================
(defn shift [piece dx]
  (let [{:keys [orientation position]} piece
        new-position (la/vec+ position (la/scale dx [1 0]))]
    (assoc piece :position new-position :orientation orientation)))

(defn fall [piece dy]
  (let [{:keys [orientation position]} piece
        new-position (la/vec+ position (la/scale dy [0 1]))]
    (assoc piece :position new-position :orientation orientation)))

(defn check-right-wall-collision [piece width]
  (loop [current-piece piece]
    (let [piece-parts (parts current-piece)
          rightmost (u/rightmost piece-parts)]
      (if (< rightmost width)
        current-piece
        (recur (shift current-piece -1))))))

(defn check-left-wall-collision [piece]
  (loop [current-piece piece]
    (let [piece-parts (parts current-piece)
          leftmost (u/leftmost piece-parts)]
      (if (>= leftmost 0)
        current-piece
        (recur (shift current-piece 1))))))

(defn rotate [piece]
  (let [{:keys [position orientation]} piece
        R (la/rotation-matrix (* 0.5 Math/PI))
        new-orientation (la/lin-transform R orientation)
        ;; NOTE need to round elements of the orientation vector because
        ;; coord has to have integer elements
        new-orientation-int [(Math/round (first new-orientation)) 
                             (Math/round (last new-orientation))]
        new-position (compensate-rotation piece position new-orientation-int)]
    (assoc piece :position new-position :orientation new-orientation-int)))


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
      [ 0 -1] (la/vec+ position [-2  1]))))

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
    position))

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
      [ 0 -1] (la/vec+ position [-2  1]))))

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
      [ 0 -1] (la/vec+ position [-3  3]))))

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
      [ 0 -1] (la/vec+ position [ 0  0]))))

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
      [ 0 -1] (la/vec+ position [-2  1]))))

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
      [ 0 -1] (la/vec+ position [ 0  0]))))


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
