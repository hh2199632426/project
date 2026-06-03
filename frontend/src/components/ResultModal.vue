<template>
  <div v-if="visible" class="modal-overlay">
    <div class="modal-content">
      <h2>🎉 比赛结束 🎉</h2>
      <div class="winner">
        获胜者: <span class="winner-name">玩家 {{ winnerId }}</span>
      </div>
      
      <div class="stats">
        <div class="player-stat" v-for="player in players" :key="player.id">
          <h3>玩家 {{ player.id }}</h3>
          <p>答对: {{ player.correctCount }}</p>
          <p>答错: {{ player.wrongCount }}</p>
          <p>最终距离: {{ Math.round(player.position) }}m</p>
        </div>
      </div>

      <div class="actions">
        <button class="btn restart" @click="$emit('restart')">再来一局</button>
        <button class="btn exit" @click="$emit('exit')">退出</button>
      </div>
    </div>
  </div>
</template>

<script setup>
defineProps({
  visible: Boolean,
  winnerId: Number,
  players: Array
});

defineEmits(['restart', 'exit']);
</script>

<style scoped>
.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: rgba(0, 0, 0, 0.7);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 1000;
}

.modal-content {
  background: white;
  padding: 30px;
  border-radius: 10px;
  width: 500px;
  text-align: center;
}

.winner {
  font-size: 24px;
  margin: 20px 0;
  color: #67c23a;
  font-weight: bold;
}

.stats {
  display: flex;
  justify-content: space-around;
  margin-bottom: 30px;
}

.player-stat {
  padding: 10px;
  background: #f5f7fa;
  border-radius: 5px;
  width: 45%;
}

.actions {
  display: flex;
  justify-content: center;
  gap: 20px;
}

.btn {
  padding: 10px 25px;
  border: none;
  border-radius: 5px;
  cursor: pointer;
  color: white;
}

.restart { background-color: #409eff; }
.exit { background-color: #909399; }
</style>
