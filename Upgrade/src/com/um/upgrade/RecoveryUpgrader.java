package com.um.upgrade;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.um.upgrade.util.FileUtil;
import com.um.upgrade.util.UpgradeUtil;
import com.um.upgrade.util.ZipUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * 用户系统下升级Recovery分区
 * Created by ziliang.nong on 14-8-15.
 */
public class RecoveryUpgrader {
    private final String TAG = RecoveryUpgrader.class.getSimpleName()+"----U668";
    private final boolean LOGE = true;

    private final long WRITING_DEV_PART_TIME = 1000*2;
    private final int PARTITION_TYPE_RECOVERY = 0;
    private final int PARTITION_TYPE_LOADERDB = 1;

    private Context context;
    private OnUpgradeCompletedListener onUpgradeCompletedListener;
    private List<Callback> callbacks;
    private String srcZipPath;
    private String dstUnzipDirPath;
    private String recoveryPartPath;
    private String loaderdbPartPath;
    private String recoveryImgName;
    private String loaderdbImgName;

    public void setContext(Context context) {
        this.context = context;
    }

    /**
     * 设置下载的升级压缩文件路径
     * @param zipFilePath
     */
    public void setSrcZipPath(String zipFilePath) {
        srcZipPath = zipFilePath;
    }

    /**
     * 设置升级压缩文件解压路径（临时目录，升级完成会删除）
     * @param unZipDirPath
     */
    public void setDstUnzipDirPath(String unZipDirPath) {
        dstUnzipDirPath = unZipDirPath;
    }

    /**
     * 设置Recovery分区路径
     * @param recoveryPartPath
     */
    public void setRecoveryPartPath(String recoveryPartPath) {
        this.recoveryPartPath = recoveryPartPath;
    }

    /**
     * 设置Loaderdb分区路径
     * @param loaderdbPartPath
     */
    public void setLoaderdbPartPath(String loaderdbPartPath) {
        this.loaderdbPartPath = loaderdbPartPath;
    }

    /**
     * 设置Recovery分区升级镜像名
     * @param recoveryImgName
     */
    public void setRecoveryImgName(String recoveryImgName) {
        this.recoveryImgName = recoveryImgName;
    }

    /**
     * 设置Loaderdb分区升级镜像名
     * @param loaderdbImgName
     */
    public void setLoaderdbImgName(String loaderdbImgName) {
        this.loaderdbImgName = loaderdbImgName;
    }

    /**
     * 设置Recovery分区升级完成监听器
     * @param listener
     */
    public void setUpgradeCompletedListener(OnUpgradeCompletedListener listener) {
        this.onUpgradeCompletedListener = listener;
    }

    /**
     * 启动Recovery分区升级
     */
    public void upgradeRecovery() {
        int diviIndex = srcZipPath.lastIndexOf("/");
        final String srcDirPath = srcZipPath.substring(0, diviIndex+1);
        final String srcFileName = srcZipPath.substring(diviIndex+1);
        diviIndex = recoveryPartPath.lastIndexOf("/");
        final String recoveryPartDirPath = recoveryPartPath.substring(0, diviIndex+1);
        final String recoveryPartFileName = recoveryPartPath.substring(diviIndex+1);
        diviIndex = loaderdbPartPath.lastIndexOf("/");
        final String loaderdbPartDirPath = loaderdbPartPath.substring(0, diviIndex+1);
        final String loaderdbPartFileName = loaderdbPartPath.substring(diviIndex+1);

        addCallback(new Callback() {
            @Override
            public void onOnUnZipCompleted(String srcDirPath, String srcZipName, String unzipDirPath) {
                if (LOGE) Log.d(TAG, "unzipCompleted");
                if (FileUtil.dirExist(srcDirPath)) {
                    FileUtil.deleteDir(srcDirPath+srcZipName);
                    if (LOGE) Log.d(TAG, "deleted source zip file path: "+srcDirPath+srcZipName);
                }
                writeDevPart(unzipDirPath, loaderdbImgName, loaderdbPartDirPath, loaderdbPartFileName,
                        PARTITION_TYPE_LOADERDB, true);
            }

            @Override
            public void onWriteLoaderdbCompleted(String srcDirPath, String srcFileName, String dstPartDirPath, String dstPartFileName) {
                if (LOGE) Log.d(TAG, "writeLoaderdbCompleted");
                writeDevPart(srcDirPath, recoveryImgName, recoveryPartDirPath, recoveryPartFileName,
                        PARTITION_TYPE_RECOVERY, true);
            }

            @Override
            public void onWriteRecoveryCompleted(String srcDirPath, String srcFileName, String dstPartDirPath, String dstPartFileName) {
                if (LOGE) Log.d(TAG, "writeRecoveryCompleted");
                if (FileUtil.dirExist(srcDirPath)) {
                    boolean result = FileUtil.deleteDir(srcDirPath);
                    if (LOGE) Log.d(TAG, "deleted unzip dirctory path: "+
                            srcDirPath+" result: "+result);
                }
                callOnUpgradeCompleted(srcDirPath, srcFileName, dstPartDirPath, dstPartFileName);
            }
        });
        unzipFile(srcDirPath, srcFileName, dstUnzipDirPath);
    }

    /**
     * 解压升级包压缩文件
     * @param srcDirPath
     * @param srcFileName
     * @param dstDirPath
     */
    private void unzipFile(final String srcDirPath, final String srcFileName, final String dstDirPath) {
        if (LOGE) Log.d(TAG, "unzipFlie");
        new Thread() {
            @Override
            public void run() {
                super.run();
                if (FileUtil.dirExist(dstDirPath)) {
                    if (LOGE) Log.d(TAG, "existed directory path: "+dstDirPath);
                    boolean result = FileUtil.deleteDir(dstDirPath);
                    if (LOGE) Log.d(TAG, "deleted directory path: "+dstDirPath+" result: "+result);
                }
                if (FileUtil.fileExist(srcDirPath+srcFileName)) {
                    if (LOGE) Log.d(TAG, "existed source zip file path: "+srcDirPath+srcFileName);
                    try {
                        ZipUtil.upZipFile(new File(srcDirPath+srcFileName), dstDirPath);
//                      ZipUtil.unZipFolder(zipPath, folderPath);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    callOnUnzipCompleted(srcDirPath, srcFileName, dstDirPath);
                }
            }
        }.start();
    }

    /**
     * 写Recovery分区
     * @param srcDirPath
     * @param srcFileName
     * @param partDirPath
     * @param partFileName
     * @param cover
     */
    private void writeDevPart(final String srcDirPath, final String srcFileName, final String partDirPath, final String partFileName,
                              final int partType, final boolean cover) {
        if (LOGE) Log.d(TAG, "writeDevPart");
        if (LOGE) Log.d(TAG, "source unzip directory path: "+srcDirPath);
        if (LOGE) Log.d(TAG, "source unzip file name: "+srcFileName);
        if (LOGE) Log.d(TAG, "partition directory path: "+partDirPath);
        if (LOGE) Log.d(TAG, "partition file name: "+partFileName);
        if (FileUtil.fileExist(partDirPath+partFileName)) {
            if (LOGE) Log.d(TAG, "existed partition path: "+partDirPath+partFileName);
            if (cover) {
                boolean result = FileUtil.deleteFile(partDirPath+partFileName);
                if (LOGE) Log.d(TAG, "deleted partition path: "+partDirPath+partFileName+" result: "+result);
            } else {
                return;
            }
        }

        if (FileUtil.fileExist(srcDirPath+srcFileName)) {
            if (LOGE) Log.d(TAG, "existed source unzip file: "+srcDirPath+srcFileName);
            new Thread() {
                @Override
                public void run() {
                    super.run();
                    if (LOGE) Log.d(TAG, "writing device partition, partition type: "+partType);
                    UpgradeUtil.upgradeDevPart(context, srcDirPath + srcFileName, partType);
                    Handler handler = new Handler(Looper.getMainLooper());
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            switch (partType) {
                                case PARTITION_TYPE_RECOVERY:
                                    callOnWriteRecoveryCompleted(srcDirPath, srcFileName, partDirPath, partFileName);
                                    break;
                                case PARTITION_TYPE_LOADERDB:
                                    callOnWriteLoaderdbCompleted(srcDirPath, srcFileName, partDirPath, partFileName);
                                    break;
                                default:
                                    break;
                            }
                        }
                    }, WRITING_DEV_PART_TIME);
                }
            }.start();
        }
    }

    /**
     * 添加回调
     * @param callback
     * @return
     */
    private boolean addCallback(Callback callback) {
        if (callbacks == null) {
            callbacks = new ArrayList<Callback>();
        }

        return callbacks.add(callback);
    }

    /**
     * 解压完成回调
     * @param srcDirPath
     * @param srcZipName
     * @param unZipDirPath
     */
    private void callOnUnzipCompleted(String srcDirPath, String srcZipName, String unZipDirPath) {
        if (callbacks != null) {
            for (Callback callback : callbacks) {
                callback.onOnUnZipCompleted(srcDirPath, srcZipName, unZipDirPath);
            }
        }
    }

    /**
     * 写Loaderdb分区完成回调
     * @param srcDirPath
     * @param srcFileName
     * @param dstDirPath
     * @param dstFileName
     */
    private void callOnWriteLoaderdbCompleted(String srcDirPath, String srcFileName, String dstDirPath, String dstFileName) {
        if (callbacks != null) {
            for (Callback callback : callbacks) {
                callback.onWriteLoaderdbCompleted(srcDirPath, srcFileName, dstDirPath, dstFileName);
            }
        }
    }

    /**
     * 写Recovery分区完成回调
     * @param srcDirPath
     * @param srcFileName
     * @param dstDirPath
     * @param dstFileName
     */
    private void callOnWriteRecoveryCompleted(String srcDirPath, String srcFileName, String dstDirPath, String dstFileName) {
        if (callbacks != null) {
            for (Callback callback : callbacks) {
                callback.onWriteRecoveryCompleted(srcDirPath, srcFileName, dstDirPath, dstFileName);
            }
        }
    }

    /**
     * 回调接口
     */
    private interface Callback {
        void onOnUnZipCompleted(String srcDirPath, String srcZipName, String unzipDirPath);
        void onWriteLoaderdbCompleted(String srcDirPath, String srcFileName, String dstPartDirPath, String dstPartFileName);
        void onWriteRecoveryCompleted(String srcDirPath, String srcFileName, String dstPartDirPath, String dstPartFilePath);
    }

    /**
     * 调用Recovery分区升级完成监听器
     * @param srcDirPath
     * @param srcFileName
     * @param dstPartDirPath
     * @param dstPartFileName
     */
    private void callOnUpgradeCompleted(String srcDirPath, String srcFileName, String dstPartDirPath, String dstPartFileName) {
        if (onUpgradeCompletedListener != null) {
            onUpgradeCompletedListener.onUpgradeCompleted(srcDirPath, srcFileName, dstPartDirPath, dstPartFileName);
        }
    }

    /**
     * Recovery分区升级完成监听器接口
     */
    public interface OnUpgradeCompletedListener {
        public void onUpgradeCompleted(String srcDirPath, String srcFileName, String dstPartDirPath, String dstPartFilePath);
    }
}
