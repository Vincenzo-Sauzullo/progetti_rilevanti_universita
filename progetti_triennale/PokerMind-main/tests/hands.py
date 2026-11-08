import unittest

from game.card import Card
from game.hand import Hand
from game.handranking import HandRanking
from game.rank import Rank
from game.suit import Suit


class Hands(unittest.TestCase):
    def test_royal_flush(self):
        royal_flush = Hand(
            Card(Rank.TEN,   Suit.HEARTS),
            Card(Rank.JACK,  Suit.HEARTS),
            Card(Rank.QUEEN, Suit.HEARTS),
            Card(Rank.KING,  Suit.HEARTS),
            Card(Rank.ACE,   Suit.HEARTS),
        )

        self.assertEqual(royal_flush.ranking, HandRanking.ROYAL_FLUSH)

    def test_straight_flush(self):
        royal_flush = Hand(
            Card(Rank.TWO,   Suit.HEARTS),
            Card(Rank.THREE, Suit.HEARTS),
            Card(Rank.FOUR,  Suit.HEARTS),
            Card(Rank.FIVE,  Suit.HEARTS),
            Card(Rank.SIX,   Suit.HEARTS),
        )

        self.assertEqual(royal_flush.ranking, HandRanking.STRAIGHT_FLUSH)

    def test_straight_flush2(self):
        straight_flush = Hand(
            Card(Rank.TWO,   Suit.HEARTS),
            Card(Rank.THREE, Suit.HEARTS),
            Card(Rank.FOUR,  Suit.HEARTS),
            Card(Rank.FIVE,  Suit.HEARTS),
            Card(Rank.SIX,   Suit.HEARTS),
        )

        self.assertEqual(straight_flush.ranking, HandRanking.STRAIGHT_FLUSH)

    def test_four_of_a_kind(self):
        four_of_a_kind = Hand(
            Card(Rank.TWO,   Suit.HEARTS),
            Card(Rank.TWO,   Suit.DIAMONDS),
            Card(Rank.TWO,   Suit.CLUBS),
            Card(Rank.TWO,   Suit.SPADES),
            Card(Rank.TEN,   Suit.HEARTS),
        )

        self.assertEqual(four_of_a_kind.ranking, HandRanking.FOUR_OF_A_KIND)

    def test_full_house(self):
        full_house = Hand(
            Card(Rank.TWO,   Suit.HEARTS),
            Card(Rank.TWO,   Suit.DIAMONDS),
            Card(Rank.TWO,   Suit.CLUBS),
            Card(Rank.EIGHT, Suit.SPADES),
            Card(Rank.EIGHT, Suit.HEARTS),
        )

        self.assertEqual(full_house.ranking, HandRanking.FULL_HOUSE)

    def test_flush(self):
        flush = Hand(
            Card(Rank.TWO,   Suit.HEARTS),
            Card(Rank.FOUR,  Suit.HEARTS),
            Card(Rank.SIX,   Suit.HEARTS),
            Card(Rank.EIGHT, Suit.HEARTS),
            Card(Rank.TEN,   Suit.HEARTS),
        )

        self.assertEqual(flush.ranking, HandRanking.FLUSH)

    def test_straight(self):
        straight = Hand(
            Card(Rank.TWO,   Suit.HEARTS),
            Card(Rank.THREE, Suit.DIAMONDS),
            Card(Rank.FOUR,  Suit.CLUBS),
            Card(Rank.FIVE,  Suit.SPADES),
            Card(Rank.SIX,   Suit.HEARTS),
        )

        self.assertEqual(straight.ranking, HandRanking.STRAIGHT)

    def test_three_of_a_kind(self):
        three_of_a_kind = Hand(
            Card(Rank.TWO,   Suit.HEARTS),
            Card(Rank.TWO,   Suit.DIAMONDS),
            Card(Rank.TWO,   Suit.CLUBS),
            Card(Rank.EIGHT, Suit.SPADES),
            Card(Rank.TEN,   Suit.HEARTS),
        )

        self.assertEqual(three_of_a_kind.ranking, HandRanking.THREE_OF_A_KIND)

    def test_two_pair(self):
        two_pair = Hand(
            Card(Rank.TWO,   Suit.HEARTS),
            Card(Rank.TWO,   Suit.DIAMONDS),
            Card(Rank.SIX,   Suit.CLUBS),
            Card(Rank.SIX,   Suit.SPADES),
            Card(Rank.TEN,   Suit.HEARTS),
        )

        self.assertEqual(two_pair.ranking, HandRanking.TWO_PAIR)

    def test_pair(self):
        pair = Hand(
            Card(Rank.TWO,   Suit.HEARTS),
            Card(Rank.TWO,   Suit.DIAMONDS),
            Card(Rank.SIX,   Suit.CLUBS),
            Card(Rank.EIGHT, Suit.SPADES),
            Card(Rank.TEN,   Suit.HEARTS),
        )

        self.assertEqual(pair.ranking, HandRanking.PAIR)

    def test_high_card(self):
        high_card = Hand(
            Card(Rank.TWO,   Suit.HEARTS),
            Card(Rank.FOUR,  Suit.DIAMONDS),
            Card(Rank.SIX,   Suit.CLUBS),
            Card(Rank.EIGHT, Suit.SPADES),
            Card(Rank.TEN,   Suit.HEARTS),
        )

        self.assertEqual(high_card.ranking, HandRanking.HIGH_CARD)

    def test_compare1(self):
        player1 = Hand(
            Card(Rank.TWO,   Suit.HEARTS),
            Card(Rank.TWO,   Suit.CLUBS),
        )

        player2 = Hand(
            Card(Rank.TWO,   Suit.DIAMONDS),
            Card(Rank.TWO,   Suit.SPADES),
        )

        self.assertTrue(player1 == player2)

    def test_compare2(self):
        player1 = Hand(
            Card(Rank.TWO,   Suit.HEARTS),
            Card(Rank.TWO,   Suit.CLUBS),
            Card(Rank.THREE, Suit.CLUBS)
        )

        player2 = Hand(
            Card(Rank.TWO,   Suit.DIAMONDS),
            Card(Rank.TWO,   Suit.SPADES),
            Card(Rank.THREE, Suit.SPADES)
        )

        self.assertTrue(player1 == player2)

    def test_compare3(self):
        player1 = Hand(
            Card(Rank.TWO,   Suit.HEARTS),
            Card(Rank.TWO,   Suit.CLUBS),
            Card(Rank.ACE,   Suit.CLUBS)
        )

        player2 = Hand(
            Card(Rank.TWO,   Suit.DIAMONDS),
            Card(Rank.TWO,   Suit.SPADES),
            Card(Rank.FOUR,  Suit.SPADES)
        )

        self.assertTrue(player1 > player2)


if __name__ == "__main__":
    unittest.main()
