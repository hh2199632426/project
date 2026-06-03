import bgmUrl from '../assets/audio/race-bgm.mp3';

let audio = null;
let enabled = true;
let unlockHandlerAdded = false;

const ensureAudio = () => {
  if (audio) return audio;
  audio = new Audio(bgmUrl);
  audio.loop = true;
  audio.preload = 'auto';
  audio.volume = 0.3;
  return audio;
};

export const isBgmEnabled = () => enabled;

export const setBgmEnabled = (nextEnabled) => {
  enabled = Boolean(nextEnabled);
  const a = ensureAudio();
  if (!enabled) {
    a.pause();
    try {
      a.currentTime = 0;
    } catch {
    }
  }
};

export const playBgm = async () => {
  if (!enabled) return;
  const a = ensureAudio();
  if (!unlockHandlerAdded) {
    unlockHandlerAdded = true;
    const unlock = () => {
      if (!enabled) return;
      const p = a.play();
      if (p && typeof p.then === 'function') {
        p.then(() => {
          document.removeEventListener('click', unlock);
          document.removeEventListener('touchstart', unlock);
        }).catch(() => {
        });
      } else {
        document.removeEventListener('click', unlock);
        document.removeEventListener('touchstart', unlock);
      }
    };
    document.addEventListener('click', unlock, { once: false });
    document.addEventListener('touchstart', unlock, { once: false });
  }
  const p = a.play();
  if (p && typeof p.then === 'function') {
    await p;
  }
};

export const stopBgm = () => {
  const a = ensureAudio();
  a.pause();
  try {
    a.currentTime = 0;
  } catch {
  }
};

export const isBgmPlaying = () => {
  if (!audio) return false;
  return !audio.paused;
};

