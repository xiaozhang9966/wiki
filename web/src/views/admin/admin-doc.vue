<template>
  <a-layout>
    <a-layout-content
        :style="{ background: '#fff', padding: '24px', margin: 0, minHeight: '80px'  }"
    >

      <a-row :gutter="24">
<!--        建议 16+*8n-->
        <a-col :span="8">
          <p>
            <a-form layout="inline" :model="param">
              <a-form-item>
                <a-button type="primary" @click="handleQuery()">
                  查询
                </a-button>
              </a-form-item>
              <a-form-item>
                <a-button type="primary" @click="add()" >
                  新增
                </a-button>
              </a-form-item>
            </a-form>
          </p>
          <!--列,key id,数据doc,分页,等待框,分页执行方法-->
          <a-table
              v-if="level1.length > 0"
              :columns="columns"
              :row-key="record=>record.id"
              :data-source="level1"
              :loading="loading"
              :pagination="false"
              size="small"
              :defaultExpandAllRows="true"
          >
            <!--
              v-if="level1.lenght > 0"
              :defaultExpandAllRows="true"      增加一个
              初始时，是否展开所有行
              必须加v-if="level1.lenght > 0"  否则不生效-->

            <template #name="{ text, record }">
<!--              <img class="image" v-if="cover" :src="cover" alt="avatar"/> &lt;!&ndash;渲染图片&ndash;&gt;-->
              {{record.sort}} {{text}}
            </template>
            <template v-slot:action="{ text, record }">
              <a-space size="small">
                <a-button type="primary" @click="edit(record)" size="small">
                  编辑
                </a-button>
                <a-popconfirm
                    title="删除后不可删除?"
                    ok-text="是"
                    cancel-text="否"
                    @confirm="handleDelete(record.id)"
                >
                  <a-button type="primary" size="small" style="background-color:red;border: none">
                    <!-- 更改按钮颜色   style="background-color:red;border: none" -->
                    删除
                  </a-button>
                </a-popconfirm>

              </a-space>
            </template>
          </a-table>
        </a-col>
        <a-col :span="16">
<!--       开始，为左右分开，富文档添加一个保存按钮-->
          <p>
            <a-form layout="inline" :model="param">
              <a-form-item>
                <a-button type="primary" @click="handleSave()">
                  保存
                </a-button>
              </a-form-item>
            </a-form>
          </p>
<!--       结束，为左右分开，富文档添加一个保存按钮   -->
          <!--弹出表单  》》》 修改为左右布局 -->
          <a-form :model="doc" layout="vertical">
            <a-form-item>
              <a-input v-model:value="doc.name" placeholder="名称"/>
            </a-form-item>
            <a-form-item>
              <a-tree-select
                  v-model:value="doc.parent"
                  style="width: 100%"
                  :dropdown-style="{ maxHeight: '400px', overflow: 'auto' }"
                  :tree-data="treeSelectData"
                  placeholder="请选择父文档"
                  tree-default-expand-all
                  :replaceFields="{title: 'name',key: 'id',value: 'id'}"
              >
              </a-tree-select>
            </a-form-item>
            <a-form-item>
              <a-input v-model:value="doc.sort" placeholder="顺序"/>
            </a-form-item>
            <a-form-item>
              <a-button type="primary" @click="handlePreviewContent()">
                <EyeOutlined/>
                内容预览
              </a-button>
            </a-form-item>
            <a-form-item> <!--这里就是导入wangditor富文本的代码-->
              <div id="content">
              </div>
            </a-form-item>
          </a-form>
        </a-col>
      </a-row>

      <a-drawer width="900" placement="right" :closable="false" :visible="drawerVisible" @close="onDrawerClose">
        <div class="wangeditor" :innerHTML="previewHtml"></div>
      </a-drawer>

    </a-layout-content>
  </a-layout>

<!--  <a-modal-->
<!--      title="文档"-->
<!--      v-model:visible="modalVisible"-->
<!--      :confirm-loading="modalLoading"-->
<!--      ok-text="确认"-->
<!--      @ok="handleModalOk"-->
<!--  >-->
<!--  </a-modal>-->

</template>

<script lang="ts">
import {defineComponent, onMounted, ref} from 'vue';//写上onMounted VUE3.0 setup集成了 导入ref 做响应式数据
import axios from 'axios';
import { message} from "ant-design-vue";
import {Tool} from "@/util/tool";
import {useRoute} from "vue-router";
import E from 'wangeditor';

export default defineComponent({
  name: 'AdminDoc',
  setup() {
    const route = useRoute();
    console.log("路由: ",route);
    console.log("route.path: ",route);
    console.log("route.query: ",route.query);
    console.log("route.param ",route.params);
    console.log("route.fullpath: ",route.fullPath);
    console.log("route.name:",route.name);
    console.log("route.meta:",route.meta);
    const param = ref();
    param.value = {};
    const docs = ref();//响应式数据 获取的书籍实时反馈到页面上

    const loading = ref(false);

    //因为树选择组件的属性状态，会随着当前编辑的节点而变化，所以单独声明一个响应式变量
    const treeSelectData = ref();
    treeSelectData.value = [];

    const columns = [//页面的响应变量 不是数据的响应变量 代表就是这个表格里面有多少个数据 下面数据我们自己定义的
      {
        title: '名称',
        dataIndex: 'name',
        slots: { customRender: 'name'}
      },
      {
        title: 'Action',
        key: 'action',
        slots: {customRender: 'action'}
      }
    ];
    /**
     * 一级文档树，children属性就是二级文档
     * [{
     *   id: "",
     *   name: "",
     *   children:[{
     *     id: "",
     *     name: "",
     *   }]
     * }]
     */
    const level1 = ref(); //一级文档树，children属性就是二级文档
    level1.value = [];
    /**
     * 数据查询
     **/
    const handleQuery = () => {
      loading.value = true;
      level1.value = [];
      axios.get("/doc/all/" + route.query.ebookId).then((response) => {
        loading.value = false;
        const data = response.data;
        if (data.success) {
          docs.value = data.content;
          console.log("原始数组：",docs.value);

          level1.value = [];
          level1.value = Tool.array2Tree(docs.value, 0);
          console.log("树形结构：",level1);

          //富文档下拉框初始化，相当于点击新增
          treeSelectData.value = Tool.copy(level1.value) || [];
          //为选择树添加一个“无”
          treeSelectData.value.unshift({id: 0,name: '无'});
          //重置分页按钮
        }else {
          message.error(data.message);
        }
      });
    };


    /**
     * 将某节点及其子孙节点全部置为dosable
     */
    const setDisable=(treeSelectData:any,id:any)=>{
      //consoLe.Log(treeselectData, id );
      // 逦历数组，即逦历某一层节点
      for (let i =0;i < treeSelectData.length;i++){
        const node = treeSelectData[i];
        if (node.id === id){
          // 如果当前节点就是目标节点
          console.log("disable",node);
          // 将目标节点设置为disabled
          node.disabled = true;

          //遍历所有子节点，将所有子节点全部加上disabled
          const children = node.children;
          if (Tool.isNotEmpty(children)){
            for (let j =0;j<children.length;j++){
              setDisable(children,children[j].id)
            }
          }
        }else {
          //如果当前节点不是目标节点，则到其子节点再找找看
          const children = node.children;
          if (Tool.isNotEmpty(children)){
            setDisable(children,id);
          }
        }
      }
    };

    const ids: Array<string> = [];
    /**
     * 查找整根树枝
     */
    const getDeleteIds =(treeSelectData:any,id:any)=>{
      //consoLe.Log(treeselectData, id );
      // 逦历数组，即逦历某一层节点
      for (let i =0;i < treeSelectData.length;i++){
        const node = treeSelectData[i];
        if (node.id === id){
          // 如果当前节点就是目标节点
          console.log("delete",node);
          // 将目标ID放入结果集ids
          // node.disabled = true;
          ids.push(id);
          //遍历所有子节点
          const children = node.children;
          if (Tool.isNotEmpty(children)){
            for (let j =0;j<children.length;j++){
              getDeleteIds(children,children[j].id)
            }
          }
        }else {
          //如果当前节点不是目标节点，则到其子节点再找找看
          const children = node.children;
          if (Tool.isNotEmpty(children)){
            getDeleteIds(children,id);
          }
        }
      }
    };

    /**表单*/

    const doc = ref();
    doc.value = {ebookId: route.query.ebookId};
    const modalVisible = ref(false);
    const modalLoading = ref(false);

    const editor = new E('#content');
    //修改这个值，默认是500，把输入框给遮挡住了，把它变成0，因为在它之上没有东西可以覆盖它
    //越大就排在顶级，就会把其他的元素给挡住
    editor.config.zIndex = 0;


    const handleSave = () => {       //9.16 文档管理页面布局修改   把原来的handleModalOK  修改成handleSave
      modalLoading.value = true;
      doc.value.content = editor.txt.html();
      axios.post("/doc/save",doc.value).then((response) => {
        modalLoading.value = false;
        const data = response.data;  //commonResp
        if(data.success){
          //modalVisible.value = false;
          message.success("保存成功！")
          //重新加载列表
          handleQuery();
        }else {
          message.error(data.message);
        }
      });
    };

    /**
     * 内容查询
     **/
    const handleQueryContent = () => {
      axios.get("/doc/find-content/" + doc.value.id).then((response) => {
        loading.value = false;
        const data = response.data;
        if (data.success) {
          editor.txt.html((data.content))
        }else {
          message.error(data.message);
        }
      });
    };
    /**
     * 编辑
     */
    const edit = ( record:any ) =>{
      //先清空富文本框
      editor.txt.html("");
      modalVisible .value = true;
      doc.value = Tool.copy(record);
      handleQueryContent();
      //不能选择当前节点及其所有子孙节点，作为父节点，会使树断开
      treeSelectData.value = Tool.copy(level1.value);
      setDisable(treeSelectData.value,record.id);
      //为选择树添加一个“无”
      treeSelectData.value.unshift({id: 0,name: '无'});
      // setTimeout(function (){
      //   editor.create();
      // },100);
    };
    /**
     * 添加
     */
    const add = () =>{
      //先清空富文本框
      editor.txt.html("");
      modalVisible .value = true;
      doc.value={
        ebookId: route.query.ebookId
      };

      treeSelectData.value = Tool.copy(level1.value) || [];
      treeSelectData.value.unshift({id: 0,name: '无'});
      // setTimeout(function (){
      //   editor.create();
      // },100);
    };


    /**
     * 删除
     */
    const handleDelete = ( id:number ) =>{
      console.log(level1.value,id);
      getDeleteIds(level1.value,id);
      console.log(ids);
      axios.delete("/doc/delete/" + ids.join(",") ).then((response)=>{
        const data = response.data;  //commonResp
        if(data.success){
          //重新加载列表
          handleQuery();
        }
      })
    };

    //-----------------富文本预览---------------
    const drawerVisible = ref(false);
    const previewHtml = ref();
    const handlePreviewContent = () =>{
      const html = editor.txt.html();
      previewHtml.value = html;
      drawerVisible.value = true;
    };
    const onDrawerClose = () => {
      drawerVisible.value = false;
    }

    onMounted(() => {
      editor.create();
      handleQuery();
    });

    return {
      param,
      //docs,//表格
      level1,
      columns,
      loading,

      edit,
      add,
      handleDelete,
      treeSelectData,

      doc,
      modalVisible,
      modalLoading,
      handleSave,       // handleModalOk,
      handleQuery,

      drawerVisible,
      previewHtml,
      handlePreviewContent,
      onDrawerClose
    }
  }
});
</script>

<!-- #scoped表示当前组件才有用 -->
<style scoped>
.image {
  width: 50px;
  height: 50px;
  line-height: 50px;
  border-radius: 8%;
  margin: 5px 0;
}

</style>