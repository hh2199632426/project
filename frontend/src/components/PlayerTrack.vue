<template>
  <div class="player-track">
    <div class="track-header">
      <h3>玩家 {{ playerId }} 赛道</h3>
      <div class="speed">速度: {{ Math.round(speed) }} km/h</div>
    </div>
    
    <div class="track-container">
      <!-- 左草地 -->
      <div class="grass grass-left">
        <div class="trees" :style="{ backgroundPositionY: treePosition + 'px' }"></div>
      </div>

      <!-- 中间道路 -->
      <div class="road">
        <!-- 赛道中间的分道线 -->
        <div class="track-line" :style="{ backgroundPositionY: treePosition + 'px' }"></div>
        
        <!-- 终点线 (相对于剩余距离移动) -->
        <div class="finish-line" :style="{ bottom: getFinishLinePosition() + 'px' }">
          FINISH
        </div>
        
        <!-- 赛车 (固定位置，主体) -->
        <div class="car" :class="{ 'shake': (currentFeedback?.type === 'wrong' || currentFeedback?.type === 'timeout') && showFeedback }">
          <RaceCar :color="playerId === 2 ? 'yellow' : 'red'" />
          
          <!-- 反馈动画 -->
          <div v-if="showFeedback" class="feedback-effect" :class="currentFeedback?.type" :key="currentFeedback?.timestamp">
            <span v-if="currentFeedback?.type === 'correct'">🚀 加速!</span>
            <span v-if="currentFeedback?.type === 'wrong'">⚠️ 减速!</span>
            <span v-if="currentFeedback?.type === 'timeout'">⏰ 超时!</span>
          </div>
        </div>
      </div>

      <!-- 右草地 -->
      <div class="grass grass-right">
        <div class="trees" :style="{ backgroundPositionY: treePosition + 'px' }"></div>
      </div>
    </div>
    
    <div class="progress-bar">
      <div class="progress" :style="{ height: progressPercentage + '%' }"></div>
    </div>
  </div>
</template>

<script setup>
import { computed, ref, watch } from 'vue';
import RaceCar from './RaceCar.vue';

const props = defineProps({
  playerId: Number,
  position: Number,
  speed: Number,
  distanceToEnd: Number,
  isRacing: Boolean,
  feedback: Object // { type: 'correct'|'wrong'|'timeout', timestamp: number }
});

const visualTrackLength = 800;
const showFeedback = ref(false);
const currentFeedback = ref(null);
let shakeTimeout = null;
let feedbackTimeout = null;

// 监听反馈变化，触发动画
watch(() => props.feedback, (newVal) => {
  if (newVal && newVal.timestamp) {
    // 强制重置状态，确保动画能重新触发
    showFeedback.value = false;
    currentFeedback.value = null;
    
    // 清除之前的定时器
    if (shakeTimeout) clearTimeout(shakeTimeout);
    if (feedbackTimeout) clearTimeout(feedbackTimeout);

    // 使用 nextTick 确保 DOM 更新后再触发新动画
    setTimeout(() => {
      currentFeedback.value = newVal;
      showFeedback.value = true;
      
      // 1.5秒后隐藏反馈
      feedbackTimeout = setTimeout(() => {
        showFeedback.value = false;
      }, 1500);

      // 0.5秒后清除震动状态（配合CSS动画时长）
      if (newVal.type === 'wrong' || newVal.type === 'timeout') {
          shakeTimeout = setTimeout(() => {
              // 这里不需要显式清除，因为下一次 feedback 变化会重置
              // 但为了保险，我们可以让 computed 依赖 timestamp
          }, 500);
      }
    }, 10);
  }
}, { deep: true });

const totalLength = computed(() => {
  const position = Number(props.position || 0);
  const distanceToEnd = Number(props.distanceToEnd || 0);
  return Math.max(1, position + distanceToEnd);
});

const progressRatio = computed(() => {
  const position = Number(props.position || 0);
  return Math.min(1, Math.max(0, position / totalLength.value));
});

const progressPercentage = computed(() => progressRatio.value * 100);

// 模拟背景移动位置 (树木和赛道线)
const treePosition = ref(0);

let animationFrameId;

const animateBackground = () => {
  if (props.isRacing && props.speed > 0) {
    // 背景向下移动，模拟车向前开
    // 速度系数调整，让视觉效果更流畅
    treePosition.value += props.speed * 0.15;
  }
  animationFrameId = requestAnimationFrame(animateBackground);
};

watch(() => props.isRacing, (newVal) => {
  if (newVal) {
    animationFrameId = requestAnimationFrame(animateBackground);
  } else {
    cancelAnimationFrame(animationFrameId);
  }
});

const getFinishLinePosition = () => {
  const minBottom = 50;
  return minBottom + (1 - progressRatio.value) * (visualTrackLength - minBottom);
};
</script>

<style scoped>
.player-track {
  background-color: #333;
  color: white;
  padding: 10px;
  border-radius: 10px;
  display: flex;
  flex-direction: column;
  height: 100%;
  overflow: hidden;
  position: relative;
}

.track-header {
  display: flex;
  justify-content: space-between;
  margin-bottom: 10px;
  z-index: 10;
}

.track-container {
  flex-grow: 1;
  position: relative;
  overflow: hidden;
  margin: 0 20px;
  background-color: #333;
  display: flex; /* 使用 Flex 布局分列 */
}

/* 草地区域 */
.grass {
  width: 25%; /* 左右各占 25% */
  background-color: #81C784;
  position: relative;
  overflow: hidden;
}

.grass-left {
  border-right: 2px solid rgba(255,255,255,0.2);
}

.grass-right {
  border-left: 2px solid rgba(255,255,255,0.2);
}

/* 道路区域 */
.road {
  width: 50%; /* 中间占 50% */
  background-color: #555;
  position: relative;
  overflow: hidden;
}

/* 树木样式 */
.trees {
  position: absolute;
  top: -100px; /* 缓冲区域 */
  bottom: -100px;
  left: 0;
  right: 0;
  background-image: url('../assets/tree.svg');
  background-repeat: repeat-y;
  background-position: center top; /* 树木居中 */
  background-size: 60% auto; /* 树木大小 */
  opacity: 0.9;
}

.track-line {
  position: absolute;
  top: -100px;
  bottom: -100px;
  left: 50%;
  width: 4px;
  transform: translateX(-50%);
  background: repeating-linear-gradient(
    to bottom,
    rgba(255, 255, 255, 0.8) 0,
    rgba(255, 255, 255, 0.8) 40px,
    transparent 40px,
    transparent 80px
  );
  z-index: 1;
}

.finish-line {
  position: absolute;
  width: 100%;
  height: 30px;
  background: repeating-linear-gradient(
    45deg,
    white,
    white 10px,
    black 10px,
    black 20px
  );
  text-align: center;
  color: red;
  font-weight: bold;
  font-size: 14px;
  line-height: 30px;
  z-index: 2;
  box-shadow: 0 2px 5px rgba(0,0,0,0.5);
}

.car {
  position: absolute;
  bottom: 50px; /* 固定位置 */
  left: 50%;
  transform: translateX(-50%);
  width: 80px; /* 增大赛车尺寸 */
  height: auto;
  z-index: 10;
  filter: drop-shadow(0 5px 5px rgba(0,0,0,0.5));
}

.car svg {
  width: 100%;
  height: auto;
  display: block;
}

.shake {
  animation: shake 0.5s cubic-bezier(.36,.07,.19,.97) both;
}

@keyframes shake {
  10%, 90% { transform: translateX(-52%); }
  20%, 80% { transform: translateX(-48%); }
  30%, 50%, 70% { transform: translateX(-54%); }
  40%, 60% { transform: translateX(-46%); }
}

.feedback-effect {
  position: absolute;
  top: -40px;
  left: 50%;
  transform: translateX(-50%);
  font-weight: bold;
  font-size: 16px;
  white-space: nowrap;
  padding: 4px 8px;
  border-radius: 4px;
  animation: floatUp 1s ease-out forwards;
  z-index: 20;
}

.feedback-effect.correct {
  color: #67c23a;
  text-shadow: 0 0 3px rgba(0,0,0,0.8);
}

.feedback-effect.wrong {
  color: #f56c6c;
  text-shadow: 0 0 3px rgba(0,0,0,0.8);
}

.feedback-effect.timeout {
  color: #e6a23c;
  text-shadow: 0 0 3px rgba(0,0,0,0.8);
}

@keyframes floatUp {
  0% { opacity: 0; transform: translate(-50%, 0); }
  20% { opacity: 1; transform: translate(-50%, -10px); }
  80% { opacity: 1; transform: translate(-50%, -20px); }
  100% { opacity: 0; transform: translate(-50%, -30px); }
}

.progress-bar {
  position: absolute;
  right: 5px;
  top: 50px;
  bottom: 20px;
  width: 5px;
  background-color: #444;
  border-radius: 3px;
}

.progress {
  position: absolute;
  bottom: 0;
  width: 100%;
  background-color: #67c23a;
  border-radius: 3px;
  transition: height 0.1s linear;
}
</style>
