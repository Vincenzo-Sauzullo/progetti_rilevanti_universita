import json
import socket
from typing import Any

from game.card import Card
from service.interface import Interface


_BUFFER_SIZE = 1024


class Proxy(Interface):
    def __init__(self, port: int):
        self._socket = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
        self._socket.connect(("127.0.0.1", port))

    def _request(self, **kwargs) -> tuple[bool, Any]:
        # Send
        self._socket.send(json.dumps(kwargs).encode("utf-8"))
        # Receive
        data = self._socket.recv(_BUFFER_SIZE)

        returned = json.loads(data.decode("utf-8"))
        ok = returned["ok"]
        data = returned.get("data")

        return ok, data

    def show(self):
        return self._request(action="show")

    def reveal(self):
        return self._request(action="reveal")

    def guess(self, card: Card):
        return self._request(
            action="guess",
            card=dict(
                rank=card.rank.representation,
                suit=card.suit.representation
            )
        )

    def new_game(self):
        return self._request(action="new_game")

    def close(self):
        self._socket.close()
