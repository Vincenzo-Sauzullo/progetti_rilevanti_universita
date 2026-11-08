<p align="center" ><img src="./static/logo.png" alt="PokerMind logo" width="50%" /></p>

# PokerMind

PokerMind is a Python-based game in which participants attempt to deduce a concealed card, communicating via a client-server (Proxy-Skeleton) architecture.

## Table of contents

> - [Requirements](#requirements)
> - [Installation](#installation)
>   - [pipx](#pipx)
> - [Usage](#usage)
>   - [Server](#server)
>   - [Client](#client)
>     - [Commands](#commands)
> - [Authors](#authors)
> - [License](#license)

## Requirements

- Python 3.11 or higher

No external dependency is required for installation.

## Installation

Clone the repository:

```bash
git clone https://github.com/Midblyte/PokerMind.git
cd PokerMind
```

You can either do one of the following:
- run the files directly (run `src/server.py` and `src/client.py`); or
- install with `pip install .` the project locally in a virtual environment (run `pokermindserver` and `pokermind`).

The only downside to the former approach is having to move to the folder each time.
In case of the latter, it is to reactivate the virtual environment in every new terminal.

### pipx

If available, you can use [pipx](https://github.com/pypa/pipx):
```bash
pipx install git+https://github.com/Midblyte/PokerMind.git
```

Now, the commands `pokermindserver` and `pokermind` will be available globally.

## Usage

### Server

> Both `python3 src/server.py` and `pokermindserver` are equivalent.

Run the server, optionally specifying a threshold and port:

```bash
pokermindserver [--threshold THRESHOLD] [--port PORT]
```

- `--threshold`, `-t`: Integer in [4..31], controls the game threshold (default: 12)
- `--port`, `-p`: Port to listen on (default: 2231)

**Example:**
```bash
pokermindserver --threshold 10 --port 2231
```

### Client

> Both `python3 src/client.py` and `pokermind` are equivalent.

The client connects to the server and provides several subcommands:

```bash
pokermind [--port PORT] <command> [options]
```

- `--port`, `-p`: Server port (default: 2231)

#### Commands

- `show` - Show the current game state.

  ```bash
  pokermind show
  ```

- `reveal` - Reveal the concealed card.

  ```bash
  pokermind reveal
  ```

  - `--training-mode, -T [ITERATIONS]`: Run in training mode for a number of iterations (default: 100, if specified without value)
  - `--numerical`: Output numerical data (non-normalized integers; requires training mode)

  **Examples:**
  ```bash
  pokermind reveal --training-mode
  pokermind reveal --training-mode 50 --numerical
  ```

- `guess <rank> <suit>` - Submit a guess for the concealed card.  
  - `<rank>`: One of A, 2–10, J, Q, K (case-insensitive: a, j, q, k allowed)
  - `<suit>`: One of hearts, diamonds, clubs, spades

  **Example:**
  ```bash
  pokermind guess 7 spades
  ```

- `new_game` - Start a new game session.

  ```bash
  pokermind new_game
  ```

## Authors
- Midblyte
- AngelogyPythITA
- Sauz926

## License

[MIT](LICENSE), as it's an educational project.
