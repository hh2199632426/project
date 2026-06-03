<template>
  <div v-if="visible" class="modal-overlay">
    <div class="modal-content">
      <h2>选择游戏模式</h2>
      
      <div class="section-title">模式类型</div>
      <div class="mode-list">
        <div 
          v-for="(typeConfig, typeKey) in modeTypes" 
          :key="typeKey" 
          class="mode-card"
          :class="{ active: selectedModeType === typeKey }"
          @click="selectedModeType = typeKey"
        >
          <h3>{{ typeConfig.label }}</h3>
          <p class="mode-desc" v-if="typeKey === 'timeLimit'">
            ⏱️ 每题限时 {{ typeConfig.timeLimitSeconds }} 秒
          </p>
          <p class="mode-desc" v-else>
            ✨ 无时间限制
          </p>
        </div>
      </div>

      <div class="section-title">赛道距离</div>
      <div class="mode-list">
        <div 
          v-for="(mode, key) in modes" 
          :key="key" 
          class="mode-card"
          :class="{ active: selectedModeKey === key }"
          @click="selectedModeKey = key"
        >
          <h3>{{ mode.label }}</h3>
          <p class="mode-desc">
            🏁 赛道长度: {{ mode.trackLength }}m <br>
            📝 题目数量: {{ mode.questionCount }}题
          </p>
        </div>
      </div>
      
      <!-- 科目设置 -->
      <div class="category-settings">
        <button class="btn settings-btn" @click="showCategoryModal = true">
          ⚙️ 设置科目
        </button>
        <p class="selected-info" v-if="selectedCategoryNames.length > 0">
          已选科目: {{ selectedCategoryNames.join('、') }}
        </p>
        <p class="selected-info" v-else>
          已选科目: 全部科目
        </p>
      </div>

      <div class="modal-footer">
        <button class="btn confirm" @click="confirmSelection">确认</button>
        <button class="btn close" @click="$emit('update:visible', false)">取消</button>
      </div>
    </div>

    <!-- 科目选择子弹窗 -->
    <div v-if="showCategoryModal" class="modal-overlay category-modal">
      <div class="modal-content">
        <h3>选择科目</h3>
        <div class="category-list">
          <label class="category-item all-select">
            <input 
              type="checkbox" 
              :checked="isAllSelected" 
              @change="toggleAll"
            >
            <span>全选</span>
          </label>
          <div class="category-grid">
            <label 
              v-for="cat in categories" 
              :key="cat.id" 
              class="category-item"
            >
              <input 
                type="checkbox" 
                :value="cat.id" 
                v-model="tempSelectedCategories"
              >
              <span>{{ cat.name }}</span>
            </label>
          </div>
        </div>
        <div class="modal-footer">
          <button class="btn save" @click="saveCategories">确定</button>
          <button class="btn cancel" @click="showCategoryModal = false">取消</button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, watch } from 'vue';
import { useGameStore } from '../store/gameStore';
import { gameConfig } from '../config/gameConfig';

const props = defineProps({
  visible: Boolean,
  modes: Object
});

const emit = defineEmits(['update:visible', 'select']);
const gameStore = useGameStore();

const showCategoryModal = ref(false);
const tempSelectedCategories = ref([]);
const modeTypes = gameConfig.modeTypes;
const selectedModeKey = ref(gameStore.selectedModeKey);
const selectedModeType = ref(gameStore.selectedModeType || 'normal');

// 获取所有目录
onMounted(() => {
  gameStore.fetchCategories();
});

watch(() => props.visible, (newVal) => {
  if (newVal) {
    selectedModeKey.value = gameStore.selectedModeKey;
    selectedModeType.value = gameStore.selectedModeType || 'normal';
  }
});

// 初始化临时选中的目录
watch(() => showCategoryModal.value, (newVal) => {
  if (newVal) {
    if (gameStore.selectedCategories.length === 0) {
      // 如果store中为空，表示全选，则UI上全部勾选
      tempSelectedCategories.value = categories.value.map(cat => cat.id);
    } else {
      tempSelectedCategories.value = [...gameStore.selectedCategories];
    }
  }
});

const categories = computed(() => gameStore.categories);
const selectedCategoryNames = computed(() => {
  if (gameStore.selectedCategories.length === 0) return [];
  return categories.value
    .filter(cat => gameStore.selectedCategories.includes(cat.id))
    .map(cat => cat.name);
});

const isAllSelected = computed(() => {
  return tempSelectedCategories.value.length === categories.value.length && categories.value.length > 0;
});

const toggleAll = (e) => {
  if (e.target.checked) {
    tempSelectedCategories.value = categories.value.map(cat => cat.id);
  } else {
    tempSelectedCategories.value = [];
  }
};

const saveCategories = () => {
  // 如果选择了全部，或者一个都没选（默认全选），则存空数组
  if (tempSelectedCategories.value.length === categories.value.length) {
    gameStore.setSelectedCategories([]);
  } else {
    gameStore.setSelectedCategories([...tempSelectedCategories.value]);
  }
  showCategoryModal.value = false;
};

const confirmSelection = () => {
  emit('select', { modeKey: selectedModeKey.value, modeType: selectedModeType.value });
  emit('update:visible', false);
};
</script>

<style scoped>
.active {
  border-color: #409eff;
  background-color: #ecf5ff;
}
.section-title {
  text-align: left;
  font-weight: bold;
  margin-bottom: 10px;
  color: #606266;
}
.confirm {
    background-color: #67c23a;
    color: white;
}
.confirm:hover {
    background-color: #85ce61;
}
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

.category-modal {
  z-index: 1001;
  background: rgba(0, 0, 0, 0.5);
}

.modal-content {
  background: white;
  padding: 30px;
  border-radius: 12px;
  width: 500px;
  max-width: 90vw;
  text-align: center;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.2);
}

h2 {
  margin-bottom: 24px;
  color: #333;
}

.mode-list {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 16px;
  margin-bottom: 24px;
}

.mode-card {
  border: 2px solid #e4e7ed;
  border-radius: 8px;
  padding: 16px;
  cursor: pointer;
  transition: all 0.3s;
}

.mode-card:hover {
  border-color: #409eff;
  background-color: #ecf5ff;
  transform: translateY(-2px);
}

.mode-card h3 {
  margin: 0 0 10px;
  color: #303133;
}

.mode-desc {
  margin: 0;
  color: #606266;
  font-size: 14px;
  line-height: 1.6;
}

.category-settings {
  margin-bottom: 24px;
  padding: 15px;
  background: #f8f9fa;
  border-radius: 8px;
  border: 1px dashed #dcdfe6;
}

.settings-btn {
  background: #67c23a;
  color: white;
  margin-bottom: 10px;
}

.settings-btn:hover {
  background: #85ce61;
}

.selected-info {
  font-size: 12px;
  color: #909399;
  margin: 0;
}

.category-list {
  text-align: left;
  max-height: 300px;
  overflow-y: auto;
  margin: 20px 0;
  padding: 10px;
  border: 1px solid #ebeef5;
  border-radius: 4px;
}

.category-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 10px;
}

.category-item {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 8px;
  cursor: pointer;
  border-radius: 4px;
  transition: background 0.2s;
}

.category-item:hover {
  background: #f5f7fa;
}

.all-select {
  margin-bottom: 10px;
  padding-bottom: 10px;
  border-bottom: 1px solid #ebeef5;
  font-weight: bold;
}

.modal-footer {
  display: flex;
  justify-content: center;
  gap: 12px;
}

.btn {
  padding: 10px 20px;
  border: none;
  border-radius: 6px;
  cursor: pointer;
  font-size: 14px;
  transition: all 0.3s;
}

.save {
  background-color: #409eff;
  color: white;
}

.save:hover {
  background-color: #66b1ff;
}

.cancel {
  background-color: #909399;
  color: white;
}

.close {
  background-color: #909399;
  color: white;
  padding: 10px 40px;
}

.close:hover {
  background-color: #82848a;
}
</style>
