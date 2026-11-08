from functools import cache

from game.card import Card
from game.gamewinner import GameWinner
from game.hand import Hand


class Game:
    def __init__(self, player1: tuple[Card, ...], player2: tuple[Card, ...], card: Card):
        self.player1 = player1
        self.player2 = player2
        self.card = card

        # Round 1
        self.hand1round1 = Hand(*player1)
        self.hand2round1 = Hand(*player2)

        # Round 2
        self.hand1round2 = Hand(*player1, card)
        self.hand2round2 = Hand(*player2, card)

    @property
    @cache
    def round1(self) -> GameWinner:
        if self.hand1round1 == self.hand2round1:
            return GameWinner.TIE
        elif self.hand1round1 > self.hand2round1:
            return GameWinner.P1
        else:
            return GameWinner.P2

    @property
    @cache
    def round2(self) -> GameWinner:
        if self.hand1round2 == self.hand2round2:
            return GameWinner.TIE
        elif self.hand1round2 > self.hand2round2:
            return GameWinner.P1
        else:
            return GameWinner.P2

    def __repr__(self):
        r1, r2 = self.round1, self.round2
        r1t = f"{r1.name} wins" if r1 is not GameWinner.TIE else "Tie"
        r2t = f"{r2.name} wins" if r2 is not GameWinner.TIE else "Tie"

        r1s = '>' if r1 is GameWinner.P1 else ('<' if r1 is GameWinner.P2 else '=')
        r2s = '>' if r2 is GameWinner.P1 else ('<' if r2 is GameWinner.P2 else '=')

        return f"""\
{'  '.join(str(c) for c in self.player1)} [P1] VS [P2] {'  '.join(str(c) for c in self.player2)}
Round 1: {self.hand1round1.ranking.name:>18} {r1s*2} {self.hand2round1.ranking.name:15} {r1t:>10}
Round 2: {self.hand1round2.ranking.name:>18} {r2s*2} {self.hand2round2.ranking.name:15} {r2t:>10}"""
