export function createInactivityLogout({ timeoutMs, lastActivityKey, authKey, onTimeout }) {
  const activityEvents = [
    'mousemove',
    'mousedown',
    'keydown',
    'scroll',
    'touchstart',
    'click',
    'wheel'
  ];

  let timerId = null;
  let started = false;
  let lastActivityAt = 0;
  let lastPersistAt = 0;

  const now = () => Date.now();

  const readStoredLastActivity = () => {
    const raw = localStorage.getItem(lastActivityKey);
    const value = raw ? Number(raw) : 0;
    return Number.isFinite(value) ? value : 0;
  };

  const persistLastActivity = (ts) => {
    localStorage.setItem(lastActivityKey, String(ts));
    lastPersistAt = now();
  };

  const setLastActivity = (ts = now()) => {
    lastActivityAt = ts;
    if (now() - lastPersistAt >= 5000) {
      persistLastActivity(ts);
    }
  };

  const clearTimer = () => {
    if (timerId !== null) {
      clearTimeout(timerId);
      timerId = null;
    }
  };

  const schedule = () => {
    clearTimer();
    const remaining = timeoutMs - (now() - lastActivityAt);
    if (remaining <= 0) {
      timeout();
      return;
    }
    timerId = setTimeout(timeout, remaining);
  };

  const timeout = () => {
    stop();
    localStorage.removeItem(lastActivityKey);
    if (typeof onTimeout === 'function') {
      onTimeout();
    }
  };

  const onActivity = () => {
    setLastActivity();
    schedule();
  };

  const onVisibilityChange = () => {
    if (document.visibilityState === 'visible') {
      const stored = readStoredLastActivity();
      if (stored > lastActivityAt) {
        lastActivityAt = stored;
      }
      schedule();
    }
  };

  const onFocus = () => {
    const stored = readStoredLastActivity();
    if (stored > lastActivityAt) {
      lastActivityAt = stored;
    }
    schedule();
  };

  const onStorage = (e) => {
    if (e.key === lastActivityKey) {
      const value = e.newValue ? Number(e.newValue) : 0;
      if (Number.isFinite(value) && value > lastActivityAt) {
        lastActivityAt = value;
      }
      schedule();
      return;
    }
    if (authKey && e.key === authKey && e.newValue === null) {
      timeout();
    }
  };

  const start = () => {
    if (started) return;
    started = true;

    const stored = readStoredLastActivity();
    lastActivityAt = stored || now();
    lastPersistAt = 0;
    persistLastActivity(lastActivityAt);

    for (const event of activityEvents) {
      window.addEventListener(event, onActivity);
    }
    document.addEventListener('visibilitychange', onVisibilityChange);
    window.addEventListener('focus', onFocus);
    window.addEventListener('storage', onStorage);

    schedule();
  };

  const stop = () => {
    if (!started) return;
    started = false;

    clearTimer();
    for (const event of activityEvents) {
      window.removeEventListener(event, onActivity);
    }
    document.removeEventListener('visibilitychange', onVisibilityChange);
    window.removeEventListener('focus', onFocus);
    window.removeEventListener('storage', onStorage);
  };

  return { start, stop };
}

