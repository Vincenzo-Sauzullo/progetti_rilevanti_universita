import sys

from log import logger

# noinspection PyUnresolvedReferences, PyProtectedMember
if getattr(sys, "_is_gil_enabled", None) is None or sys._is_gil_enabled():
    logger.debug("Using multiprocessing (assuming GIL)")
    from multiprocessing import Process as Task, Lock  # noqa
else:  # No GIL
    logger.debug("Using threading (assuming no GIL)")
    from threading import Thread as Task, Lock  # noqa
