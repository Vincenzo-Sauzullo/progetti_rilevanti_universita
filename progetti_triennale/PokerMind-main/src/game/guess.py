from enum import Enum


class Guess(Enum):
    WRONG   = 0
    PARTIAL = 1
    CORRECT = 2
