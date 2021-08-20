(ns tetris.main
  (:require [tetris.pieces :as p]
            [tetris.game-state :as gs]
            [clojure.set :as cs]
            [quil.core :as q]))


(def width 20)
(def height 30)
(def max-idx (* height width))
(def all-slots (range max-idx))

(def game (gs/game))
