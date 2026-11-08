import argparse

from game.pokermind import PokerMind
from service.skeleton import Skeleton


def main():
    parser = argparse.ArgumentParser()

    parser.add_argument("--threshold", "-t", choices=range(4, 31), default=12, metavar="[4..31]", type=int)
    parser.add_argument("--port", "-p", default=2231, type=int)

    args = parser.parse_args()

    threshold: int = args.threshold
    port: int = args.port

    pokermind = PokerMind(threshold=threshold)
    skeleton = Skeleton(port, pokermind)
    skeleton.run()


if __name__ == "__main__":
    main()
