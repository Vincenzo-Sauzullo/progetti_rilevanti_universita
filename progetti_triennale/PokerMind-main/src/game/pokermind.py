import os
import time
from random import sample as random_sample

from log import logger
from multiprocessing import Queue
from typing import Optional

from game.card import Card, DECK
from game.game import Game
from game.guess import Guess
from service.concurrency import Task, Lock


class PokerMind:
    # threshold = [4..31]
    def __init__(self, threshold: int = 4):
        self.threshold = threshold

        self.limit = min(self.threshold, os.cpu_count())
        self.games: Queue[Game] = Queue(self.limit)
        self.lock = Lock()

        self.game: Game = ...

        for i in range(self.limit):
            t = Task(target=self._generator, name=f"Generator_{i}")
            t.daemon = True
            t.start()

    def show(self) -> Game:
        with self.lock:
            return self.game

    def reveal(self) -> Card:
        with self.lock:
            return self.game.card

    def guess(self, card: Card) -> Guess:
        with self.lock:
            rank_matches = card.rank == self.game.card.rank
            suit_matches = card.suit == self.game.card.suit

            if rank_matches and suit_matches:
                return Guess.CORRECT
            elif not rank_matches and not suit_matches:
                return Guess.WRONG

            return Guess.PARTIAL

    def new_game(self) -> Game:
        self.game = self.games.get()

        logger.debug(repr(self.game))

        return self.game

    def _generator(self):
        while True:
            # logger.info("Generating...")
            start = time.time()

            while (game := self._find(iterations=1000 * self.threshold)) is None:
                logger.warning(f"Timed out, lowering threshold from {self.threshold} to {self.threshold - 1}")

                self.threshold -= 1

            end = time.time()
            logger.info(f"Generation done! ({end - start:.3} s)")

            self.games.put(game)

    def _find(self, iterations: int) -> Optional[Game]:
        for i in range(iterations):
            sample = random_sample(DECK, 9)

            player1 = tuple(sorted(sample[0:4], key=lambda k: k.rank.numeric_value))
            player2 = tuple(sorted(sample[4:8], key=lambda k: k.rank.numeric_value))

            card = sample[8]

            game = Game(player1, player2, card)

            hands = game.hand1round1, game.hand1round2, game.hand2round1, game.hand2round2

            if sum(map(lambda h: h.ranking.value, hands)) >= self.threshold and game.round1 != game.round2:
                return game
        else:
            return None
