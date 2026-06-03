<template>
  <div class="header">
    <div class="left-section">
      <button class="back-btn" @click="$emit('back')">🔙 返回</button>
      <div class="title">赛车答题PK</div>
      <button class="bgm-btn" @click="toggleBgm" :title="isPlaying ? '关闭音乐' : '开启音乐'">
        {{ isPlaying ? '🔊' : '🔇' }}
      </button>
    </div>
    <div class="distance-info">
      <div class="player-info p1">
        玩家1 距离终点: <span :class="getDistanceClass(players[0].distanceToEnd)">{{ Math.round(players[0].distanceToEnd) }}米</span>
      </div>
      <div class="vs">VS</div>
      <div class="player-info p2">
        玩家2 距离终点: <span :class="getDistanceClass(players[1].distanceToEnd)">{{ Math.round(players[1].distanceToEnd) }}米</span>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted } from 'vue';
import { isBgmEnabled, playBgm, setBgmEnabled, stopBgm } from '../utils/bgm';

const props = defineProps({
  players: {
    type: Array,
    required: true
  }
});

const isPlaying = ref(isBgmEnabled());

const toggleBgm = () => {
  if (isPlaying.value) {
    setBgmEnabled(false);
    stopBgm();
    isPlaying.value = false;
  } else {
    setBgmEnabled(true);
    playBgm();
    isPlaying.value = true;
  }
};

onMounted(() => {
  isPlaying.value = isBgmEnabled();
  if (isPlaying.value) {
    playBgm();
  }
});

onUnmounted(() => {
  stopBgm();
});

const getDistanceClass = (distance) => {
  if (distance < 100) return 'danger';
  if (distance < 400) return 'warning';
  return 'normal';
};
</script>

<style scoped>
.header {
  height: 60px;
  background-color: #333;
  color: white;
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 0 20px;
  box-shadow: 0 2px 5px rgba(0,0,0,0.2);
}

.left-section {
  display: flex;
  align-items: center;
  gap: 20px;
}

.back-btn {
  background: transparent;
  color: white;
  border: 1px solid white;
  padding: 4px 8px;
  border-radius: 4px;
  cursor: pointer;
}

.back-btn:hover {
  background: rgba(255,255,255,0.2);
}

.bgm-btn {
  background: transparent;
  border: none;
  font-size: 24px;
  cursor: pointer;
  padding: 0 10px;
  transition: transform 0.2s;
  line-height: 1;
}

.bgm-btn:hover {
  transform: scale(1.1);
}

.title {
  font-size: 24px;
  font-weight: bold;
}

.distance-info {
  display: flex;
  gap: 40px;
  align-items: center;
  font-size: 18px;
}

.player-info {
  display: flex;
  gap: 10px;
}

.p1 { color: #409eff; }
.p2 { color: #f56c6c; }

.normal { color: white; }
.warning { color: orange; font-weight: bold; }
.danger { color: red; font-weight: bold; animation: pulse 1s infinite; }

@keyframes pulse {
  0% { opacity: 1; }
  50% { opacity: 0.5; }
  100% { opacity: 1; }
}
</style>
