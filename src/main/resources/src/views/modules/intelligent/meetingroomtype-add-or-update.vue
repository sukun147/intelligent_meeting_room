<template>
  <el-dialog
    :title="!dataForm.typeId ? '新增' : '修改'"
    :close-on-click-modal="false"
    :visible.sync="visible">
    <el-form :model="dataForm" :rules="dataRule" ref="dataForm" @keyup.enter.native="dataFormSubmit()" label-width="80px">
    <el-form-item label="会议室名称" prop="typeName">
      <el-input v-model="dataForm.typeName" placeholder="会议室名称"></el-input>
    </el-form-item>
    <el-form-item label="会议室描述" prop="typeDescription">
      <el-input v-model="dataForm.typeDescription" placeholder="会议室描述"></el-input>
    </el-form-item>
    <el-form-item label="会议室设备" prop="equipment">
      <el-input v-model="dataForm.equipment" placeholder="会议室设备"></el-input>
    </el-form-item>
    <el-form-item label="启用状态（0为禁用，1为启用）" prop="typeStatus">
      <el-input v-model="dataForm.typeStatus" placeholder="启用状态（0为禁用，1为启用）"></el-input>
    </el-form-item>
    <el-form-item label="会议室类型排序" prop="typeSort">
      <el-input v-model="dataForm.typeSort" placeholder="会议室类型排序"></el-input>
    </el-form-item>
    </el-form>
    <span slot="footer" class="dialog-footer">
      <el-button @click="visible = false">取消</el-button>
      <el-button type="primary" @click="dataFormSubmit()">确定</el-button>
    </span>
  </el-dialog>
</template>

<script>
  export default {
    data () {
      return {
        visible: false,
        dataForm: {
          typeId: 0,
          typeName: '',
          typeDescription: '',
          equipment: '',
          typeStatus: '',
          typeSort: ''
        },
        dataRule: {
          typeName: [
            { required: true, message: '会议室名称不能为空', trigger: 'blur' }
          ],
          typeDescription: [
            { required: true, message: '会议室描述不能为空', trigger: 'blur' }
          ],
          equipment: [
            { required: true, message: '会议室设备不能为空', trigger: 'blur' }
          ],
          typeStatus: [
            { required: true, message: '启用状态（0为禁用，1为启用）不能为空', trigger: 'blur' }
          ],
          typeSort: [
            { required: true, message: '会议室类型排序不能为空', trigger: 'blur' }
          ]
        }
      }
    },
    methods: {
      init (id) {
        this.dataForm.typeId = id || 0
        this.visible = true
        this.$nextTick(() => {
          this.$refs['dataForm'].resetFields()
          if (this.dataForm.typeId) {
            this.$http({
              url: this.$http.adornUrl(`/intelligent/meetingroomtype/info/${this.dataForm.typeId}`),
              method: 'get',
              params: this.$http.adornParams()
            }).then(({data}) => {
              if (data && data.code === 0) {
                this.dataForm.typeName = data.meetingRoomType.typeName
                this.dataForm.typeDescription = data.meetingRoomType.typeDescription
                this.dataForm.equipment = data.meetingRoomType.equipment
                this.dataForm.typeStatus = data.meetingRoomType.typeStatus
                this.dataForm.typeSort = data.meetingRoomType.typeSort
              }
            })
          }
        })
      },
      // 表单提交
      dataFormSubmit () {
        this.$refs['dataForm'].validate((valid) => {
          if (valid) {
            this.$http({
              url: this.$http.adornUrl(`/intelligent/meetingroomtype/${!this.dataForm.typeId ? 'save' : 'update'}`),
              method: 'post',
              data: this.$http.adornData({
                'typeId': this.dataForm.typeId || undefined,
                'typeName': this.dataForm.typeName,
                'typeDescription': this.dataForm.typeDescription,
                'equipment': this.dataForm.equipment,
                'typeStatus': this.dataForm.typeStatus,
                'typeSort': this.dataForm.typeSort
              })
            }).then(({data}) => {
              if (data && data.code === 0) {
                this.$message({
                  message: '操作成功',
                  type: 'success',
                  duration: 1500,
                  onClose: () => {
                    this.visible = false
                    this.$emit('refreshDataList')
                  }
                })
              } else {
                this.$message.error(data.msg)
              }
            })
          }
        })
      }
    }
  }
</script>
