import json
import argparse

from game.card import Card
from game.suit import Suit
from game.rank import Rank
from service.proxy import Proxy


deck_ranks = ("A", "2", "3", "4", "5", "6", "7", "8", "9", "10", "J", "Q", "K", "a", "j", "q", "k")
deck_suits = ("hearts", "diamonds", "clubs", "spades")


def main():
    parser = argparse.ArgumentParser("pokermind")

    parser.add_argument("--port", "-p", default=2231, type=int)

    subparsers = parser.add_subparsers()

    show_parser = subparsers.add_parser("show")
    show_parser.set_defaults(action="show")

    reveal_parser = subparsers.add_parser("reveal")
    reveal_parser.set_defaults(action="reveal")

    reveal_parser.add_argument("--training-mode", "-T", type=int, const=100, default=False, nargs='?', metavar="ITERATIONS = 100", help="output as CSV and create that many new games", dest="training")
    reveal_parser.add_argument("--numerical", action="store_true", help="numerical output (requires training mode)")

    guess_parser = subparsers.add_parser("guess")
    guess_parser.set_defaults(action="guess")

    guess_parser.add_argument("rank", choices=deck_ranks)
    guess_parser.add_argument("suit", choices=deck_suits)

    new_game_parser = subparsers.add_parser("new_game")
    new_game_parser.set_defaults(action="new_game")

    args = parser.parse_args()

    port = args.port

    proxy = Proxy(port)

    match getattr(args, "action", None):
        case "show":
            ok, data = proxy.show()
        case "reveal":
            if (iterations := args.training) is not False:
                for _ in range(iterations):
                    ok = training(proxy, args.numerical)

                    if not ok:
                        proxy.close()
                        exit(1)
                else:
                    proxy.close()
                    exit(0)
            else:
                ok, data = proxy.reveal()
        case "new_game":
            ok, data = proxy.new_game()
        case "guess":
            rank = Rank.by_value(args.rank)
            suit = Suit.by_value(args.suit)

            card = Card(rank, suit)

            ok, data = proxy.guess(card)
        case _:
            parser.print_help()
            proxy.close()
            exit(1)

    print(json.dumps(data, indent=4))

    proxy.close()

    exit(0 if ok else 1)


def training(proxy: Proxy, numerical: bool) -> bool:
    ok_new_game, data_new_game = proxy.new_game()
    ok_reveal,   data_reveal   = proxy.reveal()

    if not (ok_new_game and ok_reveal):
        return False

    key = "value" if numerical else "result"

    print(','.join(map(str, [
        # Input

        # Unrolled version:
        #   data_new_game[player][n][kind]["value"]
        #   for player in ("player1", "player2")
        #   for n in range(4)
        #   for kind in ("rank", "suit")

        data_new_game["player1"][0]["rank"][key],
        data_new_game["player1"][0]["suit"][key],
        data_new_game["player1"][1]["rank"][key],
        data_new_game["player1"][1]["suit"][key],
        data_new_game["player1"][2]["rank"][key],
        data_new_game["player1"][2]["suit"][key],
        data_new_game["player1"][3]["rank"][key],
        data_new_game["player1"][3]["suit"][key],

        data_new_game["player2"][0]["rank"][key],
        data_new_game["player2"][0]["suit"][key],
        data_new_game["player2"][1]["rank"][key],
        data_new_game["player2"][1]["suit"][key],
        data_new_game["player2"][2]["rank"][key],
        data_new_game["player2"][2]["suit"][key],
        data_new_game["player2"][3]["rank"][key],
        data_new_game["player2"][3]["suit"][key],

        data_new_game["round1"][key],
        data_new_game["round2"][key],

        # Output

        data_reveal["card"]["rank"][key],
        data_reveal["card"]["suit"][key]
    ])))

    return True


if __name__ == "__main__":
    main()
