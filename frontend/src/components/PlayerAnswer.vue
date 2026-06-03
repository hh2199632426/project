<template>
  <div class="player-answer" :class="{ 'disabled': !isRacing }">
    <div class="header">
      <h3>玩家 {{ playerId }}</h3>
      <div v-if="modeType === 'timeLimit' && currentQuestion" class="timer-container">
        <div class="timer-bar" :style="{ width: progressPercentage + '%', backgroundColor: progressColor }"></div>
        <span class="timer-text">{{ Math.ceil(timeLeft) }}s</span>
      </div>
      <div class="stats">
        <span class="correct">✔ {{ correctCount }}</span>
        <span class="wrong">✖ {{ wrongCount }}</span>
      </div>
    </div>
    
    <div v-if="currentQuestion" class="question-box">
      <div class="question-meta">
        <span class="category-tag">{{ currentQuestion.category || '综合知识' }}</span>
      </div>
      <div class="question-text">{{ currentQuestion.questionText || currentQuestion.text }}</div>
      <div class="options">
        <button 
          v-for="(option, index) in currentQuestion.options" 
          :key="index"
          class="option-btn"
          :disabled="isAnswering"
          @click="$emit('answer', option)"
        >
          {{ option }}
        </button>
      </div>
    </div>
    <div v-else class="waiting">
      等待开始...
    </div>
  </div>
</template>

<script setup>
import { computed } from 'vue';
import { gameConfig } from '../config/gameConfig';

const props = defineProps({
  playerId: Number,
  currentQuestion: Object,
  correctCount: Number,
  wrongCount: Number,
  isRacing: Boolean,
  isAnswering: Boolean,
  timeLeft: Number,
  modeType: String
});

defineEmits(['answer']);

const totalTime = gameConfig.modeTypes.timeLimit.timeLimitSeconds;

const progressPercentage = computed(() => {
  if (props.timeLeft === undefined || props.timeLeft === null) return 0;
  return Math.min(100, Math.max(0, (props.timeLeft / totalTime) * 100));
});

const progressColor = computed(() => {
  if (props.timeLeft <= 3) return '#f56c6c'; // 红色警告
  if (props.timeLeft <= 6) return '#e6a23c'; // 黄色提醒
  return '#67c23a'; // 绿色正常
});
</script>

<style scoped>
.player-answer {
  background-color: #f0f9eb;
  padding: 20px;
  border-radius: 10px;
  box-shadow: 0 4px 6px rgba(0,0,0,0.1);
  display: flex;
  flex-direction: column;
  height: 100%;
}

.disabled {
  opacity: 0.6;
  pointer-events: none;
}

.header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
  border-bottom: 2px solid #e1e1e1;
  padding-bottom: 10px;
}

.header h3 {
  margin: 0;
  font-size: 16px;
  white-space: nowrap;
  flex-shrink: 0;
}

.timer-container {
  flex-grow: 1;
  margin: 0 12px;
  height: 20px;
  background-color: #ebeef5;
  border-radius: 10px;
  overflow: hidden;
  position: relative;
  box-shadow: inset 0 1px 3px rgba(0,0,0,0.1);
}

.timer-bar {
  height: 100%;
  transition: width 0.1s linear, background-color 0.3s;
}

.timer-text {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 12px;
  color: #606266;
  font-weight: bold;
  text-shadow: 0 0 2px rgba(255,255,255,0.8);
}

.stats {
  display: flex;
  align-items: center;
  flex-shrink: 0;
}

.stats span {
  margin-left: 10px;
  font-weight: bold;
}

.correct { color: #67c23a; }
.wrong { color: #f56c6c; }

.question-box {
  flex-grow: 1;
  display: flex;
  flex-direction: column;
}

.question-meta {
  margin-bottom: 12px;
}

.category-tag {
  background-color: #e6f7ff;
  color: #1890ff;
  border: 1px solid #91caff;
  padding: 2px 8px;
  border-radius: 4px;
  font-size: 12px;
  font-weight: 500;
}

.question-text {
  font-size: 18px;
  margin-bottom: 20px;
  line-height: 1.5;
  font-weight: 500;
}

.options {
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.option-btn {
  padding: 12px;
  background-color: white;
  border: 1px solid #dcdfe6;
  border-radius: 4px;
  cursor: pointer;
  transition: all 0.2s;
  text-align: left;
}

.option-btn:hover:not(:disabled) {
  background-color: #ecf5ff;
  border-color: #c6e2ff;
}

.option-btn:active:not(:disabled) {
  background-color: #d9ecff;
}

.waiting {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 100%;
  color: #909399;
  font-size: 16px;
}
</style>
