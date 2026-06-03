<template>
  <div class="game-container">
    <!-- 头部组件：包含终点距离显示 -->
    <Header :players="gameStore.players" @back="$emit('back')" />
    
    <!-- 核心游戏区：四列布局 -->
    <div class="game-main">
      <!-- 第一列：玩家1答题区 -->
      <PlayerAnswer 
        :playerId="1"
        :currentQuestion="gameStore.players[0].currentQuestion"
        :correctCount="gameStore.players[0].correctCount"
        :wrongCount="gameStore.players[0].wrongCount"
        :isRacing="gameStore.gameStatus === 'racing'"
        :isAnswering="gameStore.players[0].isAnswering"
        :timeLeft="gameStore.players[0].timeLeft"
        :modeType="gameStore.selectedModeType"
        @answer="(opt) => handleAnswer(1, opt)"
      />
      
      <!-- 第二列：玩家1赛车轨迹区 -->
      <PlayerTrack 
        :playerId="1"
        :position="gameStore.players[0].position"
        :speed="gameStore.players[0].speed"
        :distanceToEnd="gameStore.players[0].distanceToEnd"
        :isRacing="gameStore.gameStatus === 'racing'"
        :feedback="gameStore.players[0].feedback"
      />
      
      <!-- 第三列：玩家2赛车轨迹区 -->
      <PlayerTrack 
        :playerId="2"
        :position="gameStore.players[1].position"
        :speed="gameStore.players[1].speed"
        :distanceToEnd="gameStore.players[1].distanceToEnd"
        :isRacing="gameStore.gameStatus === 'racing'"
        :feedback="gameStore.players[1].feedback"
      />
      
      <!-- 第四列：玩家2答题区 -->
      <PlayerAnswer 
        :playerId="2"
        :currentQuestion="gameStore.players[1].currentQuestion"
        :correctCount="gameStore.players[1].correctCount"
        :wrongCount="gameStore.players[1].wrongCount"
        :isRacing="gameStore.gameStatus === 'racing'"
        :isAnswering="gameStore.players[1].isAnswering"
        :timeLeft="gameStore.players[1].timeLeft"
        :modeType="gameStore.selectedModeType"
        @answer="(opt) => handleAnswer(2, opt)"
      />
    </div>
    
    <!-- 控制区组件 -->
    <ControlArea 
      :gameStatus="gameStore.gameStatus"
      :loading="gameStore.loading"
      :errorMsg="gameStore.errorMsg"
      @startGame="modeSelectionVisible = true"
      @restartGame="gameStore.resetGame"
      @retryFetch="() => gameStore.fetchQuestionPool(gameConfig.modes?.[gameStore.selectedModeKey]?.questionCount)"
    />
    
    <!-- 规则弹窗组件 -->
    <RuleModal v-model:visible="ruleVisible" />

    <!-- 模式选择弹窗组件 -->
    <ModeSelectionModal 
      v-model:visible="modeSelectionVisible"
      :modes="gameConfig.modes"
      @select="handleModeSelect"
    />
    
    <!-- 结算弹窗组件 -->
    <ResultModal 
      v-model:visible="resultVisible"
      :winnerId="winnerId"
      :players="gameStore.players"
      @restart="gameStore.resetGame"
      @exit="gameStore.resetGame"
    />
  </div>
</template>

<script setup>
import { ref, watch, onMounted } from "vue";
import { useGameStore } from "../store/gameStore";
import { gameConfig } from "../config/gameConfig";
import Header from "../components/Header.vue";
import PlayerAnswer from "../components/PlayerAnswer.vue";
import PlayerTrack from "../components/PlayerTrack.vue";
import ControlArea from "../components/ControlArea.vue";
import RuleModal from "../components/RuleModal.vue";
import ResultModal from "../components/ResultModal.vue";
import ModeSelectionModal from "../components/ModeSelectionModal.vue";

const gameStore = useGameStore();
const ruleVisible = ref(true); // 默认显示规则
const resultVisible = ref(false);
const modeSelectionVisible = ref(false);
const winnerId = ref(null);

const handleModeSelect = ({ modeKey, modeType }) => {
  gameStore.setMode(modeKey, modeType);
  gameStore.startGame();
};

// 监听游戏状态，结束时显示结算弹窗
watch(
  () => gameStore.gameStatus,
  (newStatus) => {
    if (newStatus === "ended") {
      // 找到获胜者（先抵达终点的玩家）
      const winner = gameStore.players.find(p => p.position >= gameStore.trackLength);
      winnerId.value = winner?.id;
      resultVisible.value = true;
    } else if (newStatus === "ready") {
      resultVisible.value = false;
      // ruleVisible.value = true; // 可选：重新开始时是否显示规则
    }
  }
);

// 处理答题事件
const handleAnswer = (playerId, selectedOption) => {
  const player = gameStore.players.find(p => p.id === playerId);
  if (!player || player.isAnswering) return;
  
  player.isAnswering = true;
  
  // 判断答题对错
  const isCorrect = selectedOption === player.currentQuestion.answer;
  if (isCorrect) {
    gameStore.correctAnswer(playerId);
  } else {
    gameStore.wrongAnswer(playerId);
  }
  
  // 分配新题目（从数据库返回的题目池中抽取）
  // 延迟一点点让用户看到反馈？这里直接切换
  setTimeout(() => {
      player.currentQuestion = gameStore.getRandomQuestion();
      if (gameStore.selectedModeType === 'timeLimit') {
         player.timeLeft = gameConfig.modeTypes.timeLimit.timeLimitSeconds;
      }
      player.isAnswering = false;
  }, 200); // 200ms debounce/feedback
};

onMounted(() => {
    // 可以在这里预加载题目，或者等待用户点击开始
});
</script>

<style scoped>
.game-container {
  display: flex;
  flex-direction: column;
  height: 100vh;
  background-color: #f0f2f5;
  font-family: 'Avenir', Helvetica, Arial, sans-serif;
}

/* 四列布局样式 */
.game-main {
  display: flex;
  flex-grow: 1;
  width: 100%;
  gap: 10px;
  padding: 20px;
  box-sizing: border-box;
  overflow: hidden;
}

/* 答题区样式（第一、四列） */
.player-answer {
  width: 20%;
}

/* 赛车轨迹区样式（第二、三列） */
.player-track {
  width: 30%;
}
</style>
