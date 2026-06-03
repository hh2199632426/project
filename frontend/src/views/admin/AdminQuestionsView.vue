<template>
  <div class="table-wrap">
    <div class="toolbar-container">
      <div class="search-bar">
        <input v-model="searchQuestionText" class="input" placeholder="搜索题目内容" @keyup.enter="onSearch" />
        <select v-model="searchCategoryId" class="input">
          <option value="">所有目录</option>
          <option v-for="c in adminStore.categories" :key="c.id" :value="c.id">{{ c.name }}</option>
        </select>
        <button class="primary" @click="onSearch">搜索</button>
        <button class="reset" @click="onReset">重置</button>
      </div>
      <div class="actions">
        <button class="reset" @click="onDownloadTemplate">下载模板</button>
        <button class="primary" @click="showImportModal = true">导入题目</button>
        <button class="primary" @click="showAddModal = true">新增题目</button>
      </div>
    </div>

    <div class="table-container">
      <table>
        <thead>
          <tr>
            <th>题目</th>
            <th>目录</th>
            <th>状态</th>
            <th>创建时间</th>
            <th>操作</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="item in adminStore.questions" :key="`${item.questionText}-${item.createdAt}`">
            <td class="question">{{ item.questionText }}</td>
            <td>{{ item.category || "-" }}</td>
            <td>{{ item.isActive === 1 ? "启用" : "停用" }}</td>
            <td>{{ formatTime(item.createdAt) }}</td>
            <td>
              <button class="link-btn" @click="onEdit(item)">修改</button>
              <button class="link-btn delete" @click="onDelete(item)">删除</button>
            </td>
          </tr>
        </tbody>
      </table>
    </div>

    <div class="pagination">
      <span>共 {{ adminStore.questionsTotal }} 条</span>
      <select v-model="pageSize" @change="handleSizeChange">
        <option :value="10">10条/页</option>
        <option :value="20">20条/页</option>
        <option :value="50">50条/页</option>
        <option :value="100">100条/页</option>
      </select>
      <button :disabled="adminStore.questionsPage <= 1" @click="handlePageChange(adminStore.questionsPage - 1)">上一页</button>
      <span>第 {{ adminStore.questionsPage }} 页</span>
      <button :disabled="adminStore.questionsPage * adminStore.questionsSize >= adminStore.questionsTotal" @click="handlePageChange(adminStore.questionsPage + 1)">下一页</button>
    </div>

    <div v-if="showAddModal" class="modal-overlay">
      <div class="modal-content">
        <h3>新增题目</h3>
        <div class="form">
          <div class="form-item">
            <label>题目内容</label>
            <textarea v-model="questionText" class="input textarea" placeholder="请输入题目内容"></textarea>
          </div>
          <div class="form-item">
            <label>所属目录</label>
            <select v-model="categoryId" class="input">
              <option value="">请选择目录</option>
              <option v-for="c in adminStore.categories" :key="c.id" :value="String(c.id)">{{ c.name }}</option>
            </select>
          </div>
          <div class="form-item">
            <label>正确选项</label>
            <select v-model="correctOption" class="input">
              <option v-for="letter in addCorrectLetters" :key="letter" :value="letter">选项 {{ letter }}</option>
            </select>
          </div>
          <div class="form-item">
            <label>状态</label>
            <label class="checkbox-label">
              <input v-model="isActive" type="checkbox" />
              启用
            </label>
          </div>
          <div class="form-item">
            <label>选项 A</label>
            <input v-model="optionA" class="input" placeholder="请输入选项 A" />
          </div>
          <div class="form-item">
            <label>选项 B</label>
            <input v-model="optionB" class="input" placeholder="请输入选项 B" />
          </div>
          <div class="form-item">
            <label>选项 C</label>
            <input v-model="optionC" class="input" placeholder="请输入选项 C" />
          </div>
          <div class="form-item">
            <label>选项 D</label>
            <input v-model="optionD" class="input" placeholder="请输入选项 D" />
          </div>
        </div>
        <div class="modal-actions">
          <button class="cancel" @click="showAddModal = false">取消</button>
          <button class="primary" @click="onAdd">确认新增</button>
        </div>
      </div>
    </div>

    <div v-if="showEditModal" class="modal-overlay">
      <div class="modal-content">
        <h3>修改题目</h3>
        <div class="form">
          <div class="form-item">
            <label>题目内容</label>
            <textarea v-model="editQuestionText" class="input textarea" placeholder="请输入题目内容"></textarea>
          </div>
          <div class="form-item">
            <label>所属目录</label>
            <select v-model="editCategoryId" class="input">
              <option value="">请选择目录</option>
              <option v-for="c in adminStore.categories" :key="c.id" :value="String(c.id)">{{ c.name }}</option>
            </select>
          </div>
          <div class="form-item">
            <label>正确选项</label>
            <select v-model="editCorrectOption" class="input">
              <option v-for="letter in editCorrectLetters" :key="letter" :value="letter">选项 {{ letter }}</option>
            </select>
          </div>
          <div class="form-item">
            <label>状态</label>
            <label class="checkbox-label">
              <input v-model="editIsActive" type="checkbox" />
              启用
            </label>
          </div>
          <div class="form-item">
            <label>选项 A</label>
            <input v-model="editOptionA" class="input" placeholder="请输入选项 A" />
          </div>
          <div class="form-item">
            <label>选项 B</label>
            <input v-model="editOptionB" class="input" placeholder="请输入选项 B" />
          </div>
          <div class="form-item">
            <label>选项 C</label>
            <input v-model="editOptionC" class="input" placeholder="请输入选项 C" />
          </div>
          <div class="form-item">
            <label>选项 D</label>
            <input v-model="editOptionD" class="input" placeholder="请输入选项 D" />
          </div>
        </div>
        <div class="modal-actions">
          <button class="cancel" @click="showEditModal = false">取消</button>
          <button class="primary" @click="onUpdate">确认修改</button>
        </div>
      </div>
    </div>
    <div v-if="showImportModal" class="modal-overlay">
      <div class="modal-content">
        <h3>导入题目</h3>
        <div class="form">
          <div class="form-item">
            <label>所属目录</label>
            <select v-model="importCategoryId" class="input">
              <option value="">请选择目录</option>
              <option v-for="c in adminStore.categories" :key="c.id" :value="String(c.id)">{{ c.name }}</option>
            </select>
          </div>
          <div class="form-item">
            <label>上传文件 (Excel)</label>
            <input type="file" accept=".xlsx, .xls" class="input" @change="onFileChange" />
          </div>
          <div v-if="importError" class="error-msg">{{ importError }}</div>
        </div>
        <div class="modal-actions">
          <button class="cancel" @click="closeImportModal">取消</button>
          <button class="primary" :disabled="!importFile || !importCategoryId" @click="onImport">确认导入</button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { computed, onMounted, ref, watch } from "vue";
import { useAdminStore } from "../../store/adminStore";
import { useUiStore } from "../../store/uiStore";
import * as XLSX from "xlsx";

const adminStore = useAdminStore();
const uiStore = useUiStore();
const searchQuestionText = ref("");
const searchCategoryId = ref("");
const showAddModal = ref(false);
const showImportModal = ref(false);
const importCategoryId = ref("");
const importFile = ref(null);
const importError = ref("");
const questionText = ref("");
const optionA = ref("");
const optionB = ref("");
const optionC = ref("");
const optionD = ref("");
const correctOption = ref("A");
const categoryId = ref("");
const isActive = ref(true);
const pageSize = ref(10);
const showEditModal = ref(false);
const editQuestionId = ref(null);
const editQuestionText = ref("");
const editOptionA = ref("");
const editOptionB = ref("");
const editOptionC = ref("");
const editOptionD = ref("");
const editCorrectOption = ref("A");
const editCategoryId = ref("");
const editIsActive = ref(true);

const getAllowedLetters = (a, b, c, d) => {
  const letters = ["A", "B"];
  const cVal = String(c || "").trim();
  const dVal = String(d || "").trim();
  if (cVal) {
    letters.push("C");
  }
  if (cVal && dVal) {
    letters.push("D");
  }
  return letters;
};

const addCorrectLetters = computed(() => getAllowedLetters(optionA.value, optionB.value, optionC.value, optionD.value));
const editCorrectLetters = computed(() => getAllowedLetters(editOptionA.value, editOptionB.value, editOptionC.value, editOptionD.value));

watch(
  addCorrectLetters,
  (letters) => {
    if (!letters.includes(correctOption.value)) {
      correctOption.value = letters[0] || "A";
    }
  },
  { immediate: true }
);

watch(
  editCorrectLetters,
  (letters) => {
    if (!letters.includes(editCorrectOption.value)) {
      editCorrectOption.value = letters[0] || "A";
    }
  },
  { immediate: true }
);

const formatTime = (value) => {
  if (!value) {
    return "-";
  }
  return String(value).replace("T", " ");
};

const handlePageChange = (page) => {
  adminStore.fetchQuestions(page, pageSize.value, searchCategoryId.value, searchQuestionText.value);
};

const handleSizeChange = () => {
  adminStore.fetchQuestions(1, pageSize.value, searchCategoryId.value, searchQuestionText.value);
};

const onSearch = () => {
  adminStore.fetchQuestions(1, pageSize.value, searchCategoryId.value, searchQuestionText.value);
};

const onReset = () => {
  searchQuestionText.value = "";
  searchCategoryId.value = "";
  adminStore.fetchQuestions(1, pageSize.value, "", "");
};

const onAdd = async () => {
  const payload = {
    questionText: String(questionText.value || "").trim(),
    optionA: String(optionA.value || "").trim(),
    optionB: String(optionB.value || "").trim(),
    optionC: String(optionC.value || "").trim(),
    optionD: String(optionD.value || "").trim(),
    correctOption: correctOption.value,
    categoryId: categoryId.value ? String(categoryId.value) : null,
    isActive: isActive.value ? 1 : 0
  };
  if (!payload.questionText) {
    uiStore.addToast("请输入题目", "error");
    return;
  }
  if (!payload.categoryId) {
    uiStore.addToast("请选择目录", "error");
    return;
  }
  if (!payload.optionA || !payload.optionB) {
    uiStore.addToast("至少填写选项A与选项B", "error");
    return;
  }
  if (payload.optionD && !payload.optionC) {
    uiStore.addToast("选项请按顺序填写，不能只填D不填C", "error");
    return;
  }
  const allowedLetters = getAllowedLetters(payload.optionA, payload.optionB, payload.optionC, payload.optionD);
  if (!allowedLetters.includes(payload.correctOption)) {
    uiStore.addToast("正确选项无效", "error");
    return;
  }
  try {
    await adminStore.addQuestion(payload);
    questionText.value = "";
    optionA.value = "";
    optionB.value = "";
    optionC.value = "";
    optionD.value = "";
    correctOption.value = "A";
    categoryId.value = "";
    isActive.value = true;
    showAddModal.value = false;
    uiStore.addToast("新增成功");
  } catch (e) {
    const msg = e?.response?.data?.message || "新增失败";
    uiStore.addToast(msg, "error");
  }
};

const onDelete = async (item) => {
  const confirmed = await uiStore.showConfirm(`确定要删除题目 "${item.questionText}" 吗？`, "删除确认");
  if (!confirmed) {
    return;
  }
  try {
    await adminStore.removeQuestion(item.id);
    uiStore.addToast("删除成功");
  } catch (e) {
    const msg = e?.response?.data?.message || "删除失败";
    uiStore.addToast(msg, "error");
  }
};

const onEdit = (item) => {
  editQuestionId.value = item.id;
  editQuestionText.value = item.questionText;
  editOptionA.value = item.optionA;
  editOptionB.value = item.optionB;
  editOptionC.value = item.optionC;
  editOptionD.value = item.optionD;
  editCorrectOption.value = item.correctOption;
  editCategoryId.value = item.categoryId ? String(item.categoryId) : "";
  editIsActive.value = item.isActive === 1;
  showEditModal.value = true;
};

const onUpdate = async () => {
  const payload = {
    questionText: String(editQuestionText.value || "").trim(),
    optionA: String(editOptionA.value || "").trim(),
    optionB: String(editOptionB.value || "").trim(),
    optionC: String(editOptionC.value || "").trim(),
    optionD: String(editOptionD.value || "").trim(),
    correctOption: editCorrectOption.value,
    categoryId: editCategoryId.value ? String(editCategoryId.value) : null,
    isActive: editIsActive.value ? 1 : 0
  };
  if (!payload.questionText) {
    uiStore.addToast("请输入题目", "error");
    return;
  }
  if (!payload.categoryId) {
    uiStore.addToast("请选择目录", "error");
    return;
  }
  if (!payload.optionA || !payload.optionB) {
    uiStore.addToast("至少填写选项A与选项B", "error");
    return;
  }
  if (payload.optionD && !payload.optionC) {
    uiStore.addToast("选项请按顺序填写，不能只填D不填C", "error");
    return;
  }
  const allowedLetters = getAllowedLetters(payload.optionA, payload.optionB, payload.optionC, payload.optionD);
  if (!allowedLetters.includes(payload.correctOption)) {
    uiStore.addToast("正确选项无效", "error");
    return;
  }
  try {
    await adminStore.updateQuestion(editQuestionId.value, payload);
    showEditModal.value = false;
    uiStore.addToast("修改成功");
  } catch (e) {
    const msg = e?.response?.data?.message || "修改失败";
    uiStore.addToast(msg, "error");
  }
};

const onDownloadTemplate = () => {
  const headers = ["题目内容", "选项A", "选项B", "选项C", "选项D", "正确选项(A/B/C/D)"];
  const data = [
    ["示例题目：1+1等于几？", "1", "2", "3", "4", "B"]
  ];
  const ws = XLSX.utils.aoa_to_sheet([headers, ...data]);
  const wb = XLSX.utils.book_new();
  XLSX.utils.book_append_sheet(wb, ws, "题目模板");
  XLSX.writeFile(wb, "题目导入模板.xlsx");
};

const onFileChange = (e) => {
  const file = e.target.files[0];
  if (file) {
    importFile.value = file;
    importError.value = "";
  }
};

const closeImportModal = () => {
  showImportModal.value = false;
  importCategoryId.value = "";
  importFile.value = null;
  importError.value = "";
};

const onImport = () => {
  if (!importFile.value) {
    importError.value = "请选择文件";
    return;
  }
  if (!importCategoryId.value) {
    importError.value = "请选择目录";
    return;
  }

  const reader = new FileReader();
  reader.onload = async (e) => {
    try {
      const data = new Uint8Array(e.target.result);
      const workbook = XLSX.read(data, { type: "array" });
      const firstSheetName = workbook.SheetNames[0];
      const worksheet = workbook.Sheets[firstSheetName];
      const jsonData = XLSX.utils.sheet_to_json(worksheet, { header: 1 });

      if (jsonData.length < 2) {
        importError.value = "文件内容为空或格式不正确";
        return;
      }

      // Remove header
      const rows = jsonData.slice(1);
      const questions = [];
      
      for (const row of rows) {
        if (!row[0]) continue; // Skip empty rows
        
        // Map row to object
        // Assuming order: Question, A, B, C, D, Correct
        const q = {
          questionText: String(row[0]).trim(),
          optionA: String(row[1] || "").trim(),
          optionB: String(row[2] || "").trim(),
          optionC: String(row[3] || "").trim(),
          optionD: String(row[4] || "").trim(),
          correctOption: String(row[5] || "A").trim().toUpperCase(),
          categoryId: String(importCategoryId.value),
          isActive: 1
        };
        
        if (q.questionText && q.optionA && q.optionB) {
          if (q.optionD && !q.optionC) continue;
          const allowedLetters = getAllowedLetters(q.optionA, q.optionB, q.optionC, q.optionD);
          if (!allowedLetters.includes(q.correctOption)) continue;
          questions.push(q);
        }
      }

      if (questions.length === 0) {
        importError.value = "未找到有效的题目数据";
        return;
      }

      await adminStore.batchAddQuestions(questions);
      uiStore.addToast(`成功导入 ${questions.length} 道题目`);
      closeImportModal();
    } catch (err) {
      console.error(err);
      importError.value = "解析或导入失败，请检查文件格式";
    }
  };
  reader.readAsArrayBuffer(importFile.value);
};

onMounted(async () => {
  await adminStore.fetchCategories();
  if (adminStore.targetCategoryId !== null && adminStore.targetCategoryId !== undefined) {
    searchCategoryId.value = adminStore.targetCategoryId;
  }
  await adminStore.fetchQuestions(1, pageSize.value, searchCategoryId.value, searchQuestionText.value);
});
</script>

<style scoped>
.toolbar-container {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 12px;
}

.search-bar {
  display: flex;
  gap: 10px;
  align-items: center;
}

.reset {
  border: 1px solid #dcdfe6;
  background: white;
  color: #606266;
  padding: 10px 14px;
  border-radius: 8px;
  cursor: pointer;
  white-space: nowrap;
}

.reset:hover {
  border-color: #c0c4cc;
  color: #303133;
}

.link-btn {
  border: none;
  background: transparent;
  color: #1677ff;
  cursor: pointer;
  padding: 4px 8px;
  font-size: 14px;
}

.link-btn:hover {
  color: #0f63d6;
}

.link-btn.delete {
  color: #ff4d4f;
}

.link-btn.delete:hover {
  color: #cf1322;
}

.pagination {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
  align-items: center;
  gap: 12px;
}

.pagination select {
  padding: 6px;
  border-radius: 4px;
  border: 1px solid #dcdfe6;
}

.pagination button {
  padding: 6px 12px;
  border: 1px solid #dcdfe6;
  background: white;
  border-radius: 4px;
  cursor: pointer;
}

.pagination button:disabled {
  background: #f5f7fa;
  cursor: not-allowed;
  color: #c0c4cc;
}

.table-wrap {
  background: white;
  border-radius: 10px;
  padding: 16px;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.06);
  height: calc(100vh - 100px);
  display: flex;
  flex-direction: column;
}

.primary {
  border: none;
  background: #1677ff;
  color: white;
  padding: 10px 14px;
  border-radius: 8px;
  cursor: pointer;
  white-space: nowrap;
}

.primary:hover {
  background: #0f63d6;
}

table {
  width: 100%;
  border-collapse: collapse;
}

.table-container {
  flex: 1;
  overflow: auto;
}

th,
td {
  text-align: left;
  border-bottom: 1px solid #ebeef5;
  padding: 12px 8px;
}

th {
  color: #909399;
  font-weight: 500;
  position: sticky;
  top: 0;
  background: white;
  z-index: 1;
}

.question {
  max-width: 600px;
}

/* Modal Styles */
.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.5);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1000;
}

.modal-content {
  background: white;
  border-radius: 12px;
  padding: 24px;
  width: 500px;
  max-width: 90%;
  max-height: 90vh;
  overflow-y: auto;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
}

.modal-content h3 {
  margin-top: 0;
  margin-bottom: 20px;
  font-size: 18px;
  color: #303133;
}

.form {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.form-item {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.form-item label {
  font-size: 14px;
  color: #606266;
  font-weight: 500;
}

.input {
  border: 1px solid #dcdfe6;
  border-radius: 6px;
  padding: 8px 12px;
  outline: none;
  width: 100%;
  box-sizing: border-box;
}

.input:focus {
  border-color: #1677ff;
}

.textarea {
  min-height: 80px;
  resize: vertical;
  font-family: inherit;
}

.checkbox-label {
  display: flex;
  align-items: center;
  gap: 8px;
  cursor: pointer;
}

.modal-actions {
  margin-top: 24px;
  display: flex;
  justify-content: flex-end;
  gap: 12px;
}

.cancel {
  border: 1px solid #dcdfe6;
  background: white;
  color: #606266;
  padding: 10px 14px;
  border-radius: 8px;
  cursor: pointer;
}

.cancel:hover {
  border-color: #c0c4cc;
  color: #303133;
}

.actions {
  display: flex;
  gap: 10px;
}

.error-msg {
  color: #ff4d4f;
  font-size: 12px;
}
</style>
