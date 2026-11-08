from enum import Enum


class Suit(Enum):
    HEARTS   = "hearts",   '♥', 0, 3
    DIAMONDS = "diamonds", '♦', 1, 2
    CLUBS    = "clubs",    '♣', 2, 1
    SPADES   = "spades",   '♠', 3, 0

    def __init__(self, representation: str, symbol: str, numeric_value: int, order: int):
        self.representation = representation
        self.symbol = symbol
        self.numeric_value = numeric_value
        self.order = order

    @classmethod
    def by_value(cls, representation: str) -> "Suit":
        for suit in cls.__members__.values():
            if suit.representation == representation.lower():
                return suit
        else:
            raise ValueError

    def __eq__(self, other):
        return self is other

    def __gt__(self, other):
        return isinstance(other, Suit) and self.order > other.order

    def __hash__(self):
        return hash(self.representation)

    def __repr__(self):
        return self.representation
