(ns tetris.game-state
  (:require [tetris.pieces :as p]
            [tetris.bucket :as b]))


;; ============================================================================
;; all implemented pieces, choose one randomly
;; ============================================================================
(def implemented-pieces
  [p/gamma-piece
   p/square-piece
   p/sausage-piece
   p/step-piece
   p/step-piece-mirror
   p/tau-piece
   p/gamma-piece-mirror])

(defn choose-piece-randomly
  []
  (rand-nth implemented-pieces))

;; ============================================================================
;; Game state protocol
;; ============================================================================
(defprotocol GameState
  "Game state."
  (piece-at-bottom? [this] 
    "Check if the piece reached the bottom of the bucket.")
  (occupied? [this new-piece-position]
    "Check if the next space in piece's way is already taken.")
  (piece-sinked? [this]
    "Check if the piece is 'sinked' i.e., it is either at the bottom or 
    the next space is occupied.")
  (freeze-piece [this]
    "When piece is sinked it becomes a part of bucket contents.")
  (choose-next-piece [this]
    "Choose how to spawn a new piece.")
  (move-piece [this]
    "Drop the piece by one unit.")
  (spawn-piece [this]
    "Spawn a new piece at the top of the bucket.")
  (step [this]
    "Make a step of the game.")
  (shift-piece [this dx]
    "Shift a piece horizontally.")
  (rotate-piece [this]
    "Rotate piece.")
  (piece-overlaps? [this new-piece-position]
    "Check if the piece is overlapping with bucket contents.")
  (game-over? [this]
    "Check if the game reached terminal state."))


;; ============================================================================
;; Game
;; ============================================================================
(defrecord Tetris [piece bucket]
  GameState

  (occupied? [this new-piece-position]
    (let [{:keys [_ bucket]} this
          bucket-contents (:contents bucket)
          bucket-set (set bucket-contents)
          positions (p/parts new-piece-position)]
      (not-every? false? (map #(contains? bucket-set %) positions))))
  
  (piece-at-bottom? [this]
    (let [{:keys [piece bucket]} this
          height (:height bucket)
          pieces (p/parts piece)]
      (not-every? true? (map #(< (last %) (dec height)) pieces))))

  (piece-sinked? [this]
    (let [{:keys [piece bucket]} this
          {:keys [width]} bucket
          fallen-piece (p/fall piece 1)
          fallen-piece (p/check-left-wall-collision fallen-piece)
          fallen-piece (p/check-right-wall-collision fallen-piece width)]
      (or (occupied? this fallen-piece) (piece-at-bottom? this))))

  (freeze-piece [this]
    (let [{:keys [piece bucket]} this
          {:keys [width height contents]} bucket
          pieces (p/parts piece)
          bucket-contents contents
          new-bucket-contents (reduce conj bucket-contents pieces)
          new-bucket (tetris.bucket.Bucket. width height new-bucket-contents)
          nil-piece nil]
      (Tetris. nil-piece new-bucket)))

  (choose-next-piece [this]
    (let [{:keys [_ bucket]} this
          {:keys [width _ _]} bucket]
      ((choose-piece-randomly) 1 0 width)))

  (spawn-piece [this] 
    (let [new-piece (choose-next-piece this)]
      (assoc this :piece new-piece)))

  (move-piece [this] 
    (let [{:keys [piece bucket]} this
          {:keys [width]} bucket
          new-piece (p/fall piece 1)
          new-piece (p/check-left-wall-collision new-piece)
          new-piece (p/check-right-wall-collision new-piece width)]
      (assoc this :piece new-piece)))

  (step [this]
    (if (piece-sinked? this)
      (-> this freeze-piece spawn-piece)
      (move-piece this)))
  
  (shift-piece [this dx] 
    (let [{:keys [piece bucket]} this
          {:keys [width _ _]} bucket
          shifted-piece (p/shift piece dx)
          shifted-piece (p/check-left-wall-collision shifted-piece)
          shifted-piece (p/check-right-wall-collision shifted-piece width)
          ovelaps? (piece-overlaps? this shifted-piece)]
      (if ovelaps? 
        this
        (assoc this :piece shifted-piece))))

  (rotate-piece [this] 
    (let [{:keys [piece _]} this
          rotated-piece (p/rotate piece)
          ovelaps? (piece-overlaps? this rotated-piece)]
      (if ovelaps? 
        this
        (assoc this :piece rotated-piece))))

  (piece-overlaps? [this new-piece-position]
    (let [{:keys [_ bucket]} this
          {:keys [_ _ contents]} bucket
          pieces (p/parts new-piece-position)
          bucket-contents contents
          combined (reduce conj bucket-contents pieces)
          combined-set (set combined)
          n-combined (count combined)
          n-combined-set (count combined-set)]
      (not (= n-combined n-combined-set))))

  (game-over? [this]
    (b/overflown? (:bucket this))))

;; ============================================================================
;; Game init
;; ============================================================================
(defn init-game []
  (let[piece ((choose-piece-randomly) 0 0 5)
       bucket (b/init-bucket 10 21)]
    (->Tetris piece bucket)))
