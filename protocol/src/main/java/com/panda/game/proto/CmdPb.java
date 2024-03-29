// Generated by the protocol buffer compiler.  DO NOT EDIT!
// source: cmd.proto

package com.panda.game.proto;

public final class CmdPb {
  private CmdPb() {}
  public static void registerAllExtensions(
      com.google.protobuf.ExtensionRegistryLite registry) {
  }

  public static void registerAllExtensions(
      com.google.protobuf.ExtensionRegistry registry) {
    registerAllExtensions(
        (com.google.protobuf.ExtensionRegistryLite) registry);
  }
  /**
   * <pre>
   * 请求相应接口id
   * </pre>
   *
   * Protobuf enum {@code com.panda.game.proto.Cmd}
   */
  public enum Cmd
      implements com.google.protobuf.ProtocolMessageEnum {
    /**
     * <code>Unkown = 0;</code>
     */
    Unkown(0),
    /**
     * <pre>
     * redis订阅cmd
     * </pre>
     *
     * <code>TopicPlayerOnline = 1;</code>
     */
    TopicPlayerOnline(1),
    /**
     * <pre>
     * login
     * </pre>
     *
     * <code>LoginRq = 81;</code>
     */
    LoginRq(81),
    /**
     * <code>LoginRs = 82;</code>
     */
    LoginRs(82),
    /**
     * <code>CreateUserRq = 83;</code>
     */
    CreateUserRq(83),
    /**
     * <code>CreateUserRs = 84;</code>
     */
    CreateUserRs(84),
    /**
     * <pre>
     * gateway
     * </pre>
     *
     * <code>GatewayLoginRq = 100;</code>
     */
    GatewayLoginRq(100),
    /**
     * <code>GatewayLoginRs = 101;</code>
     */
    GatewayLoginRs(101),
    /**
     * <code>GatewayConnectRq = 102;</code>
     */
    GatewayConnectRq(102),
    /**
     * <code>GatewayConnectRs = 103;</code>
     */
    GatewayConnectRs(103),
    /**
     * <pre>
     * logic
     * </pre>
     *
     * <code>LogicLoginRq = 1001;</code>
     */
    LogicLoginRq(1001),
    /**
     * <code>LogicLoginRs = 1002;</code>
     */
    LogicLoginRs(1002),
    /**
     * <code>PlayerGetInfoRq = 1003;</code>
     */
    PlayerGetInfoRq(1003),
    /**
     * <code>PlayerGetInfoRs = 1004;</code>
     */
    PlayerGetInfoRs(1004),
    /**
     * <code>PlayerSetNameRq = 1005;</code>
     */
    PlayerSetNameRq(1005),
    /**
     * <code>PlayerSetNameRs = 1006;</code>
     */
    PlayerSetNameRs(1006),
    /**
     * <code>FriendGetInfoRq = 2001;</code>
     */
    FriendGetInfoRq(2001),
    /**
     * <code>FriendGetInfoRs = 2002;</code>
     */
    FriendGetInfoRs(2002),
    /**
     * <code>FriendApplyRq = 2003;</code>
     */
    FriendApplyRq(2003),
    /**
     * <code>FriendApplyRs = 2004;</code>
     */
    FriendApplyRs(2004),
    /**
     * <code>ClubGetInfoRq = 3001;</code>
     */
    ClubGetInfoRq(3001),
    /**
     * <code>ClubGetInfoRs = 3002;</code>
     */
    ClubGetInfoRs(3002),
    /**
     * <pre>
     * 活动列表
     * </pre>
     *
     * <code>ActivityListRq = 4001;</code>
     */
    ActivityListRq(4001),
    /**
     * <code>ActivityListRs = 4002;</code>
     */
    ActivityListRs(4002),
    /**
     * <pre>
     * 领取奖励
     * </pre>
     *
     * <code>ActivityGetRewardRq = 4003;</code>
     */
    ActivityGetRewardRq(4003),
    /**
     * <code>ActivityGetRewardRs = 4004;</code>
     */
    ActivityGetRewardRs(4004),
    UNRECOGNIZED(-1),
    ;

    /**
     * <code>Unkown = 0;</code>
     */
    public static final int Unkown_VALUE = 0;
    /**
     * <pre>
     * redis订阅cmd
     * </pre>
     *
     * <code>TopicPlayerOnline = 1;</code>
     */
    public static final int TopicPlayerOnline_VALUE = 1;
    /**
     * <pre>
     * login
     * </pre>
     *
     * <code>LoginRq = 81;</code>
     */
    public static final int LoginRq_VALUE = 81;
    /**
     * <code>LoginRs = 82;</code>
     */
    public static final int LoginRs_VALUE = 82;
    /**
     * <code>CreateUserRq = 83;</code>
     */
    public static final int CreateUserRq_VALUE = 83;
    /**
     * <code>CreateUserRs = 84;</code>
     */
    public static final int CreateUserRs_VALUE = 84;
    /**
     * <pre>
     * gateway
     * </pre>
     *
     * <code>GatewayLoginRq = 100;</code>
     */
    public static final int GatewayLoginRq_VALUE = 100;
    /**
     * <code>GatewayLoginRs = 101;</code>
     */
    public static final int GatewayLoginRs_VALUE = 101;
    /**
     * <code>GatewayConnectRq = 102;</code>
     */
    public static final int GatewayConnectRq_VALUE = 102;
    /**
     * <code>GatewayConnectRs = 103;</code>
     */
    public static final int GatewayConnectRs_VALUE = 103;
    /**
     * <pre>
     * logic
     * </pre>
     *
     * <code>LogicLoginRq = 1001;</code>
     */
    public static final int LogicLoginRq_VALUE = 1001;
    /**
     * <code>LogicLoginRs = 1002;</code>
     */
    public static final int LogicLoginRs_VALUE = 1002;
    /**
     * <code>PlayerGetInfoRq = 1003;</code>
     */
    public static final int PlayerGetInfoRq_VALUE = 1003;
    /**
     * <code>PlayerGetInfoRs = 1004;</code>
     */
    public static final int PlayerGetInfoRs_VALUE = 1004;
    /**
     * <code>PlayerSetNameRq = 1005;</code>
     */
    public static final int PlayerSetNameRq_VALUE = 1005;
    /**
     * <code>PlayerSetNameRs = 1006;</code>
     */
    public static final int PlayerSetNameRs_VALUE = 1006;
    /**
     * <code>FriendGetInfoRq = 2001;</code>
     */
    public static final int FriendGetInfoRq_VALUE = 2001;
    /**
     * <code>FriendGetInfoRs = 2002;</code>
     */
    public static final int FriendGetInfoRs_VALUE = 2002;
    /**
     * <code>FriendApplyRq = 2003;</code>
     */
    public static final int FriendApplyRq_VALUE = 2003;
    /**
     * <code>FriendApplyRs = 2004;</code>
     */
    public static final int FriendApplyRs_VALUE = 2004;
    /**
     * <code>ClubGetInfoRq = 3001;</code>
     */
    public static final int ClubGetInfoRq_VALUE = 3001;
    /**
     * <code>ClubGetInfoRs = 3002;</code>
     */
    public static final int ClubGetInfoRs_VALUE = 3002;
    /**
     * <pre>
     * 活动列表
     * </pre>
     *
     * <code>ActivityListRq = 4001;</code>
     */
    public static final int ActivityListRq_VALUE = 4001;
    /**
     * <code>ActivityListRs = 4002;</code>
     */
    public static final int ActivityListRs_VALUE = 4002;
    /**
     * <pre>
     * 领取奖励
     * </pre>
     *
     * <code>ActivityGetRewardRq = 4003;</code>
     */
    public static final int ActivityGetRewardRq_VALUE = 4003;
    /**
     * <code>ActivityGetRewardRs = 4004;</code>
     */
    public static final int ActivityGetRewardRs_VALUE = 4004;


    public final int getNumber() {
      if (this == UNRECOGNIZED) {
        throw new java.lang.IllegalArgumentException(
            "Can't get the number of an unknown enum value.");
      }
      return value;
    }

    /**
     * @param value The numeric wire value of the corresponding enum entry.
     * @return The enum associated with the given numeric wire value.
     * @deprecated Use {@link #forNumber(int)} instead.
     */
    @java.lang.Deprecated
    public static Cmd valueOf(int value) {
      return forNumber(value);
    }

    /**
     * @param value The numeric wire value of the corresponding enum entry.
     * @return The enum associated with the given numeric wire value.
     */
    public static Cmd forNumber(int value) {
      switch (value) {
        case 0: return Unkown;
        case 1: return TopicPlayerOnline;
        case 81: return LoginRq;
        case 82: return LoginRs;
        case 83: return CreateUserRq;
        case 84: return CreateUserRs;
        case 100: return GatewayLoginRq;
        case 101: return GatewayLoginRs;
        case 102: return GatewayConnectRq;
        case 103: return GatewayConnectRs;
        case 1001: return LogicLoginRq;
        case 1002: return LogicLoginRs;
        case 1003: return PlayerGetInfoRq;
        case 1004: return PlayerGetInfoRs;
        case 1005: return PlayerSetNameRq;
        case 1006: return PlayerSetNameRs;
        case 2001: return FriendGetInfoRq;
        case 2002: return FriendGetInfoRs;
        case 2003: return FriendApplyRq;
        case 2004: return FriendApplyRs;
        case 3001: return ClubGetInfoRq;
        case 3002: return ClubGetInfoRs;
        case 4001: return ActivityListRq;
        case 4002: return ActivityListRs;
        case 4003: return ActivityGetRewardRq;
        case 4004: return ActivityGetRewardRs;
        default: return null;
      }
    }

    public static com.google.protobuf.Internal.EnumLiteMap<Cmd>
        internalGetValueMap() {
      return internalValueMap;
    }
    private static final com.google.protobuf.Internal.EnumLiteMap<
        Cmd> internalValueMap =
          new com.google.protobuf.Internal.EnumLiteMap<Cmd>() {
            public Cmd findValueByNumber(int number) {
              return Cmd.forNumber(number);
            }
          };

    public final com.google.protobuf.Descriptors.EnumValueDescriptor
        getValueDescriptor() {
      if (this == UNRECOGNIZED) {
        throw new java.lang.IllegalStateException(
            "Can't get the descriptor of an unrecognized enum value.");
      }
      return getDescriptor().getValues().get(ordinal());
    }
    public final com.google.protobuf.Descriptors.EnumDescriptor
        getDescriptorForType() {
      return getDescriptor();
    }
    public static final com.google.protobuf.Descriptors.EnumDescriptor
        getDescriptor() {
      return com.panda.game.proto.CmdPb.getDescriptor().getEnumTypes().get(0);
    }

    private static final Cmd[] VALUES = values();

    public static Cmd valueOf(
        com.google.protobuf.Descriptors.EnumValueDescriptor desc) {
      if (desc.getType() != getDescriptor()) {
        throw new java.lang.IllegalArgumentException(
          "EnumValueDescriptor is not for this type.");
      }
      if (desc.getIndex() == -1) {
        return UNRECOGNIZED;
      }
      return VALUES[desc.getIndex()];
    }

    private final int value;

    private Cmd(int value) {
      this.value = value;
    }

    // @@protoc_insertion_point(enum_scope:com.panda.game.proto.Cmd)
  }

  /**
   * <pre>
   * 错误码
   * </pre>
   *
   * Protobuf enum {@code com.panda.game.proto.ErrorCode}
   */
  public enum ErrorCode
      implements com.google.protobuf.ProtocolMessageEnum {
    /**
     * <code>Ok = 0;</code>
     */
    Ok(0),
    /**
     * <pre>
     * 参数错误
     * </pre>
     *
     * <code>Param = 1;</code>
     */
    Param(1),
    /**
     * <pre>
     * 资源不足
     * </pre>
     *
     * <code>NoEnough = 2;</code>
     */
    NoEnough(2),
    /**
     * <pre>
     * 服务异常
     * </pre>
     *
     * <code>ServiceError = 3;</code>
     */
    ServiceError(3),
    /**
     * <pre>
     * 游戏服务异常
     * </pre>
     *
     * <code>GameServiceError = 4;</code>
     */
    GameServiceError(4),
    /**
     * <pre>
     * rpc超时
     * </pre>
     *
     * <code>RpcTimeOut = 10;</code>
     */
    RpcTimeOut(10),
    /**
     * <pre>
     * rpc发送异常
     * </pre>
     *
     * <code>RpcSendError = 11;</code>
     */
    RpcSendError(11),
    /**
     * <pre>
     * rpc发送失败
     * </pre>
     *
     * <code>RpcSendFailed = 12;</code>
     */
    RpcSendFailed(12),
    /**
     * <pre>
     * 用户已经存在
     * </pre>
     *
     * <code>UserExist = 101;</code>
     */
    UserExist(101),
    UNRECOGNIZED(-1),
    ;

    /**
     * <code>Ok = 0;</code>
     */
    public static final int Ok_VALUE = 0;
    /**
     * <pre>
     * 参数错误
     * </pre>
     *
     * <code>Param = 1;</code>
     */
    public static final int Param_VALUE = 1;
    /**
     * <pre>
     * 资源不足
     * </pre>
     *
     * <code>NoEnough = 2;</code>
     */
    public static final int NoEnough_VALUE = 2;
    /**
     * <pre>
     * 服务异常
     * </pre>
     *
     * <code>ServiceError = 3;</code>
     */
    public static final int ServiceError_VALUE = 3;
    /**
     * <pre>
     * 游戏服务异常
     * </pre>
     *
     * <code>GameServiceError = 4;</code>
     */
    public static final int GameServiceError_VALUE = 4;
    /**
     * <pre>
     * rpc超时
     * </pre>
     *
     * <code>RpcTimeOut = 10;</code>
     */
    public static final int RpcTimeOut_VALUE = 10;
    /**
     * <pre>
     * rpc发送异常
     * </pre>
     *
     * <code>RpcSendError = 11;</code>
     */
    public static final int RpcSendError_VALUE = 11;
    /**
     * <pre>
     * rpc发送失败
     * </pre>
     *
     * <code>RpcSendFailed = 12;</code>
     */
    public static final int RpcSendFailed_VALUE = 12;
    /**
     * <pre>
     * 用户已经存在
     * </pre>
     *
     * <code>UserExist = 101;</code>
     */
    public static final int UserExist_VALUE = 101;


    public final int getNumber() {
      if (this == UNRECOGNIZED) {
        throw new java.lang.IllegalArgumentException(
            "Can't get the number of an unknown enum value.");
      }
      return value;
    }

    /**
     * @param value The numeric wire value of the corresponding enum entry.
     * @return The enum associated with the given numeric wire value.
     * @deprecated Use {@link #forNumber(int)} instead.
     */
    @java.lang.Deprecated
    public static ErrorCode valueOf(int value) {
      return forNumber(value);
    }

    /**
     * @param value The numeric wire value of the corresponding enum entry.
     * @return The enum associated with the given numeric wire value.
     */
    public static ErrorCode forNumber(int value) {
      switch (value) {
        case 0: return Ok;
        case 1: return Param;
        case 2: return NoEnough;
        case 3: return ServiceError;
        case 4: return GameServiceError;
        case 10: return RpcTimeOut;
        case 11: return RpcSendError;
        case 12: return RpcSendFailed;
        case 101: return UserExist;
        default: return null;
      }
    }

    public static com.google.protobuf.Internal.EnumLiteMap<ErrorCode>
        internalGetValueMap() {
      return internalValueMap;
    }
    private static final com.google.protobuf.Internal.EnumLiteMap<
        ErrorCode> internalValueMap =
          new com.google.protobuf.Internal.EnumLiteMap<ErrorCode>() {
            public ErrorCode findValueByNumber(int number) {
              return ErrorCode.forNumber(number);
            }
          };

    public final com.google.protobuf.Descriptors.EnumValueDescriptor
        getValueDescriptor() {
      if (this == UNRECOGNIZED) {
        throw new java.lang.IllegalStateException(
            "Can't get the descriptor of an unrecognized enum value.");
      }
      return getDescriptor().getValues().get(ordinal());
    }
    public final com.google.protobuf.Descriptors.EnumDescriptor
        getDescriptorForType() {
      return getDescriptor();
    }
    public static final com.google.protobuf.Descriptors.EnumDescriptor
        getDescriptor() {
      return com.panda.game.proto.CmdPb.getDescriptor().getEnumTypes().get(1);
    }

    private static final ErrorCode[] VALUES = values();

    public static ErrorCode valueOf(
        com.google.protobuf.Descriptors.EnumValueDescriptor desc) {
      if (desc.getType() != getDescriptor()) {
        throw new java.lang.IllegalArgumentException(
          "EnumValueDescriptor is not for this type.");
      }
      if (desc.getIndex() == -1) {
        return UNRECOGNIZED;
      }
      return VALUES[desc.getIndex()];
    }

    private final int value;

    private ErrorCode(int value) {
      this.value = value;
    }

    // @@protoc_insertion_point(enum_scope:com.panda.game.proto.ErrorCode)
  }


  public static com.google.protobuf.Descriptors.FileDescriptor
      getDescriptor() {
    return descriptor;
  }
  private static  com.google.protobuf.Descriptors.FileDescriptor
      descriptor;
  static {
    java.lang.String[] descriptorData = {
      "\n\tcmd.proto\022\024com.panda.game.proto*\222\004\n\003Cm" +
      "d\022\n\n\006Unkown\020\000\022\025\n\021TopicPlayerOnline\020\001\022\013\n\007" +
      "LoginRq\020Q\022\013\n\007LoginRs\020R\022\020\n\014CreateUserRq\020S" +
      "\022\020\n\014CreateUserRs\020T\022\022\n\016GatewayLoginRq\020d\022\022" +
      "\n\016GatewayLoginRs\020e\022\024\n\020GatewayConnectRq\020f" +
      "\022\024\n\020GatewayConnectRs\020g\022\021\n\014LogicLoginRq\020\351" +
      "\007\022\021\n\014LogicLoginRs\020\352\007\022\024\n\017PlayerGetInfoRq\020" +
      "\353\007\022\024\n\017PlayerGetInfoRs\020\354\007\022\024\n\017PlayerSetNam" +
      "eRq\020\355\007\022\024\n\017PlayerSetNameRs\020\356\007\022\024\n\017FriendGe" +
      "tInfoRq\020\321\017\022\024\n\017FriendGetInfoRs\020\322\017\022\022\n\rFrie" +
      "ndApplyRq\020\323\017\022\022\n\rFriendApplyRs\020\324\017\022\022\n\rClub" +
      "GetInfoRq\020\271\027\022\022\n\rClubGetInfoRs\020\272\027\022\023\n\016Acti" +
      "vityListRq\020\241\037\022\023\n\016ActivityListRs\020\242\037\022\030\n\023Ac" +
      "tivityGetRewardRq\020\243\037\022\030\n\023ActivityGetRewar" +
      "dRs\020\244\037*\230\001\n\tErrorCode\022\006\n\002Ok\020\000\022\t\n\005Param\020\001\022" +
      "\014\n\010NoEnough\020\002\022\020\n\014ServiceError\020\003\022\024\n\020GameS" +
      "erviceError\020\004\022\016\n\nRpcTimeOut\020\n\022\020\n\014RpcSend" +
      "Error\020\013\022\021\n\rRpcSendFailed\020\014\022\r\n\tUserExist\020" +
      "eB\035\n\024com.panda.game.protoB\005CmdPbb\006proto3"
    };
    descriptor = com.google.protobuf.Descriptors.FileDescriptor
      .internalBuildGeneratedFileFrom(descriptorData,
        new com.google.protobuf.Descriptors.FileDescriptor[] {
        });
  }

  // @@protoc_insertion_point(outer_class_scope)
}
