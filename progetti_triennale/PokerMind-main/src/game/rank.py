from enum import Enum


class Rank(Enum):
    ACE   = ('A',  12, 0)
    TWO   = ('2',  0,  1)
    THREE = ('3',  1,  2)
    FOUR  = ('4',  2 , 3)
    FIVE  = ('5',  3 , 4)
    SIX   = ('6',  4 , 5)
    SEVEN = ('7',  5 , 6)
    EIGHT = ('8',  6 , 7)
    NINE  = ('9',  7 , 8)
    TEN   = ('10', 8 , 9)
    JACK  = ('J',  9 , 10)
    QUEEN = ('Q',  10, 11)
    KING  = ('K',  11, 12)

    def __init__(self, representation: str, numeric_value: int, comparation_value: int):
        self.representation = representation
        self.numeric_value = numeric_value
        self.comparation_value = comparation_value

    @classmethod
    def by_value(cls, representation: str) -> "Rank":
        for rank in cls.__members__.values():
            if rank.representation == representation.upper():
                return rank
        else:
            raise ValueError

    def __gt__(self, other):
        if not isinstance(other, Rank):
            return False
        return self.numeric_value > other.numeric_value

    def __repr__(self):
        return self.representation
