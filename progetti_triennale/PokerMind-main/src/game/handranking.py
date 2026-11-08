from enum import Enum, auto


# https://www.cardplayer.com/rules-of-poker/hand-rankings
class HandRanking(Enum):
    HIGH_CARD       = auto()
    PAIR            = auto()
    TWO_PAIR        = auto()
    THREE_OF_A_KIND = auto()
    STRAIGHT        = auto()
    FLUSH           = auto()
    FULL_HOUSE      = auto()
    FOUR_OF_A_KIND  = auto()
    STRAIGHT_FLUSH  = auto()
    ROYAL_FLUSH     = auto()

    def __eq__(self, other):
        return self is other

    def __gt__(self, other):
        if not isinstance(other, HandRanking):
            return False
        return self.value > other.value
