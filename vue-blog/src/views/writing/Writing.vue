<script setup>
import {MdEditor} from 'md-editor-v3';
import 'md-editor-v3/lib/style.css';
import {ref} from "vue";
import {saveFileService} from "@/api/writing.js";
import {ElMessage} from "element-plus";
import {uploadImageService} from "@/api/common.js";

const writingData = ref({
  content: '',
  name: '',
  types: new Array(5)
})

//文档上传方法
const codeSave = async () => {
  if (writingData.value.content === '' || writingData.value.name === '') {
    ElMessage.error("标题、内容或标签为空");
    return;
  }
  let result = await saveFileService(writingData.value, 0);
  if (result.data.msg === '文章上传失败') {
    ElMessage.error("文章上传失败");
    return;
  }
  clearData();
  ElMessage.success("文档上传并保存成功");
}

//图片上传方法
const onUploadImg = async (files, callback) => {
  const res = await Promise.all(
      Array.from(files).map((file) => {
        return new Promise(async (rev) => {
          const form = new FormData();
          form.append('file', file);
          await uploadImageService(form).then((res) => {
            rev(res)
          })
        });
      })
  );
  callback(res.map((item) => item.data));
}

//清除数据方法
const clearData = () => {
  writingData.value = {
    content: '',
    name: '',
    types: []
  }
}
</script>

<template>
  <br>
  <div class="all_class">
    <div class="md_class">
      <el-input clearable placeholder="文章标题" v-model="writingData.name"></el-input>
      <el-input clearable placeholder="文章所属标签" v-model="writingData.types[0]" class="label_class"></el-input>
      <el-input clearable placeholder="文章所属标签" v-model="writingData.types[1]" class="label_class"></el-input>
      <el-input clearable placeholder="文章所属标签" v-model="writingData.types[2]" class="label_class"></el-input>
      <el-input clearable placeholder="文章所属标签" v-model="writingData.types[3]" class="label_class"></el-input>
      <el-input clearable placeholder="文章所属标签" v-model="writingData.types[4]" class="label_class"></el-input>
      <MdEditor
          toolbarsExclude="['github']"
          :showCodeRowNumber='true'
          v-model="writingData.content"
          @onUploadImg="onUploadImg"
          @onSave="codeSave"
      >
      </MdEditor>
    </div>
  </div>
</template>

<style scoped>
.all_class {
  width: 100%;
  max-width: 1600px;
  margin: 0 auto;
  padding: 0 20px;
  box-sizing: border-box;
}

.md_class {
  position: relative;
  width: 100%;
}

@media screen and (max-width: 1000px) {
  .md_class {
    position: relative;
    width: 100%;
  }
}

.label_class {
  width: 14vw;
}

@media screen and (max-width: 1000px) {
  .label_class {
    width: 17.5vw;
  }
}

/* 响应式调整 */
@media (max-width: 1600px) {
  .all_class {
    max-width: 1400px;
  }
}

@media (max-width: 1400px) {
  .all_class {
    max-width: 1200px;
  }
}

@media (max-width: 1280px) {
  .all_class {
    max-width: 1100px;
  }
}

/* 平板设备 */
@media (max-width: 1024px) {
  .all_class {
    padding: 0 15px;
  }
}

/* 移动端样式 */
@media (max-width: 767px) {
  .all_class {
    padding: 0 8px;
    max-width: 100%;
    margin: 0 auto;
  }

  .label_class {
    width: 100%;
    margin-bottom: 8px;
  }
}

/* 小屏幕手机 */
@media (max-width: 480px) {
  .all_class {
    padding: 0 6px;
  }
}

/* 超小屏幕设备 */
@media (max-width: 360px) {
  .all_class {
    padding: 0 4px;
  }
}

/* 确保所有元素都使用 border-box 模型 */
* {
  box-sizing: border-box;
}
</style>