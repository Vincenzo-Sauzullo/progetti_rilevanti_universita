import json
import socket

from game.game import Game
from game.guess import Guess
from service.interface import Interface
from service.concurrency import Task
from log import logger
from game.pokermind import PokerMind
from game.card import Card
from game.suit import Suit
from game.rank import Rank


_BUFFER_SIZE = 1024


def run_function(connection: socket.socket, skeleton: "Skeleton"):
    try:
        rcv = connection.recv(_BUFFER_SIZE)
    except ConnectionResetError as _ignored:
        return

    if rcv == b"":  # Client closed connection.
        connection.close()
        return

    data = json.loads(rcv.decode("utf-8"))

    action = data.get("action")

    ok = True
    try:
        match action:
            case "show":
                result: Game = skeleton.show()

                logger.info(f"{connection.getpeername()} show")
            case "reveal":
                result: Card = skeleton.reveal()

                logger.info(f"{connection.getpeername()} reveal")
            case "guess":
                card_object: dict = data.get("card")

                rank = Rank.by_value(card_object.get("rank"))
                suit = Suit.by_value(card_object.get("suit"))

                card = Card(rank, suit)

                result: Guess = skeleton.guess(card)

                logger.info(f"{connection.getpeername()} guess {card} -> {result.name}")
            case "new_game":
                logger.info(f"{connection.getpeername()} new_game")

                result: Game = skeleton.new_game()
            case _:
                raise ValueError
    except (ValueError, KeyError):
        ok = False
        result = ...

    if isinstance(result, Game):
        response = {
            "player1": [
                {
                    "rank": {
                        "result": card.rank.representation,
                        "value": card.rank.numeric_value
                    },
                    "suit": {
                        "result": card.suit.representation,
                        "value": card.suit.numeric_value,
                    },
                } for card in result.player1
            ],
            "player2": [
                {
                    "rank": {
                        "result": card.rank.representation,
                        "value": card.rank.numeric_value
                    },
                    "suit": {
                        "result": card.suit.representation,
                        "value": card.suit.numeric_value,
                    },
                } for card in result.player2
            ],
            "round1": {"result": result.round1.name, "value": result.round1.value},
            "round2": {"result": result.round2.name, "value": result.round2.value},
        }
    elif isinstance(result, Card):
        response = {"card": {
            "rank": {
                "result": result.rank.representation,
                "value": result.rank.numeric_value
            },
            "suit": {
                "result": result.suit.representation,
                "value": result.suit.numeric_value,
            },
        }}
    elif isinstance(result, Guess):
        response = {"result": result.name, "value": result.value}
    else:
        response = {}

    payload = json.dumps({"ok": ok, "data": response}).encode("utf-8")

    connection.send(payload)


def _handle(connection: socket.socket, skeleton: "Skeleton"):
    # noinspection PyUnresolvedReferences,PyProtectedMember
    while not connection._closed:
        run_function(connection, skeleton)


class Skeleton(Interface):
    def __init__(self, port: int, service: PokerMind):
        self.port = port
        self.service = service

    def show(self) -> Game:
        return self.service.show()

    def reveal(self) -> Card:
        return self.service.reveal()

    def guess(self, card) -> Guess:
        return self.service.guess(card)

    def new_game(self) -> Game:
        return self.service.new_game()

    def run(self):
        s = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
        s.setsockopt(socket.SOL_SOCKET, socket.SO_REUSEADDR, 1)

        try:
            s.bind(("127.0.0.1", self.port))
        except OSError as error:
            logger.error("Bind error", exc_info=error)
            exit(1)

        s.listen(1)

        logger.info(f"Listening on {s.getsockname()}")
        self.service.new_game()

        while True:
            connection, address = s.accept()

            Task(target=_handle, args=(connection, self), daemon=True).start()
