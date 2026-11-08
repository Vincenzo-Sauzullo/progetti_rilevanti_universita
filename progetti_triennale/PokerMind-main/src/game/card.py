import itertools
from typing import Final

from game.rank import Rank
from game.suit import Suit


class Card:
    def __init__(self, rank: Rank, suit: Suit):
        self.rank = rank
        self.suit = suit

    def __eq__(self, other):
        return isinstance(other, Card) and self.rank == other.rank and self.suit == other.suit

    def __gt__(self, other):
        return isinstance(other, Card) and self.rank > other.rank and self.suit > other.suit

    def __hash__(self):
        return hash((self.rank, self.suit))

    def __str__(self):
        return f"{self.suit.symbol} {self.rank.representation:2}"

    def as_dict(self) -> dict[str, str]:
        return {
            "rank": self.rank.representation,
            "suit": self.suit.representation,
        }


DECK: Final[tuple[Card, ...]] = tuple(map(lambda k: Card(*k), itertools.product(Rank, Suit)))
