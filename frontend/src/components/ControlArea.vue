<template>
  <div class="control-area">
    <div v-if="loading" class="loading">题目加载中...</div>
    <div v-else-if="errorMsg" class="error">
      {{ errorMsg }}
      <button @click="$emit('retryFetch')">重试</button>
    </div>
    <div v-else>
      <button v-if="gameStatus === 'ready'" class="btn start" @click="$emit('startGame')">开始游戏</button>
      <button v-if="gameStatus === 'racing'" class="btn restart" @click="$emit('restartGame')">重新开始</button>
      <button v-if="gameStatus === 'ended'" class="btn restart" @click="$emit('restartGame')">再来一局</button>
    </div>
  </div>
</template>

<script setup>
defineProps({
  gameStatus: String,
  loading: Boolean,
  errorMsg: String
});

defineEmits(['startGame', 'restartGame', 'retryFetch']);
</script>

<style scoped>
.control-area {
  position: fixed;
  bottom: 20px;
  left: 50%;
  transform: translateX(-50%);
  z-index: 100;
  background: rgba(255, 255, 255, 0.9);
  padding: 10px 20px;
  border-radius: 20px;
  box-shadow: 0 2px 10px rgba(0,0,0,0.1);
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 10px;
}

.btn {
  padding: 10px 30px;
  font-size: 18px;
  border: none;
  border-radius: 5px;
  cursor: pointer;
  color: white;
  transition: all 0.3s;
}

.start { background-color: #67c23a; }
.start:hover { background-color: #529b2e; }

.restart { background-color: #e6a23c; }
.restart:hover { background-color: #d48806; }

.loading { color: #409eff; }
.error { color: #f56c6c; }
</style>
