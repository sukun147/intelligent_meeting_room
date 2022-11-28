<template>
  <el-dialog
    :title="!dataForm.roomId ? '新增' : '修改'"
    :close-on-click-modal="false"
    :visible.sync="visible">
    <el-form :model="dataForm" :rules="dataRule" ref="dataForm" @keyup.enter.native="dataFormSubmit()" label-width="80px">
    <el-form-item label="地址" prop="position">
      <el-input v-model="dataForm.position" placeholder="地址"></el-input>
    </el-form-item>
    <el-form-item label="允许权限等级" prop="permissionLevel">
      <el-input v-model="dataForm.permissionLevel" placeholder="允许权限等级"></el-input>
    </el-form-item>
    <el-form-item label="空闲开始时间" prop="idleStartTime">
      <el-input v-model="dataForm.idleStartTime" placeholder="空闲开始时间"></el-input>
    </el-form-item>
    <el-form-item label="空闲结束时间" prop="idleEndTime">
      <el-input v-model="dataForm.idleEndTime" placeholder="空闲结束时间"></el-input>
    </el-form-item>
    <el-form-item label="会议室类型" prop="typeId">
      <el-input v-model="dataForm.typeId" placeholder="会议室类型"></el-input>
    </el-form-item>
    <el-form-item label="启用状态（0为禁用，1为启用）" prop="roomStatus">
      <el-input v-model="dataForm.roomStatus" placeholder="启用状态（0为禁用，1为启用）"></el-input>
    </el-form-item>
    <el-form-item label="会议室排序" prop="roomSort">
      <el-input v-model="dataForm.roomSort" placeholder="会议室排序"></el-input>
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
          roomId: 0,
          position: '',
          permissionLevel: '',
          idleStartTime: '',
          idleEndTime: '',
          typeId: '',
          roomStatus: '',
          roomSort: ''
        },
        dataRule: {
          position: [
            { required: true, message: '地址不能为空', trigger: 'blur' }
          ],
          permissionLevel: [
            { required: true, message: '允许权限等级不能为空', trigger: 'blur' }
          ],
          idleStartTime: [
            { required: true, message: '空闲开始时间不能为空', trigger: 'blur' }
          ],
          idleEndTime: [
            { required: true, message: '空闲结束时间不能为空', trigger: 'blur' }
          ],
          typeId: [
            { required: true, message: '会议室类型不能为空', trigger: 'blur' }
          ],
          roomStatus: [
            { required: true, message: '启用状态（0为禁用，1为启用）不能为空', trigger: 'blur' }
          ],
          roomSort: [
            { required: true, message: '会议室排序不能为空', trigger: 'blur' }
          ]
        }
      }
    },
    methods: {
      init (id) {
        this.dataForm.roomId = id || 0
        this.visible = true
        this.$nextTick(() => {
          this.$refs['dataForm'].resetFields()
          if (this.dataForm.roomId) {
            this.$http({
              url: this.$http.adornUrl(`/intelligent/meetingroom/info/${this.dataForm.roomId}`),
              method: 'get',
              params: this.$http.adornParams()
            }).then(({data}) => {
              if (data && data.code === 0) {
                this.dataForm.position = data.meetingRoom.position
                this.dataForm.permissionLevel = data.meetingRoom.permissionLevel
                this.dataForm.idleStartTime = data.meetingRoom.idleStartTime
                this.dataForm.idleEndTime = data.meetingRoom.idleEndTime
                this.dataForm.typeId = data.meetingRoom.typeId
                this.dataForm.roomStatus = data.meetingRoom.roomStatus
                this.dataForm.roomSort = data.meetingRoom.roomSort
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
              url: this.$http.adornUrl(`/intelligent/meetingroom/${!this.dataForm.roomId ? 'save' : 'update'}`),
              method: 'post',
              data: this.$http.adornData({
                'roomId': this.dataForm.roomId || undefined,
                'position': this.dataForm.position,
                'permissionLevel': this.dataForm.permissionLevel,
                'idleStartTime': this.dataForm.idleStartTime,
                'idleEndTime': this.dataForm.idleEndTime,
                'typeId': this.dataForm.typeId,
                'roomStatus': this.dataForm.roomStatus,
                'roomSort': this.dataForm.roomSort
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
