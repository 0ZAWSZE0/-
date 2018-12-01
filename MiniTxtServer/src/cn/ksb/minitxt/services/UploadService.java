package cn.ksb.minitxt.services;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.UUID;

import cn.ksb.minitxt.common.constants.NovelConstants;
import cn.ksb.minitxt.common.entity.DataTransfer;
import cn.ksb.minitxt.common.entity.Novel;
import cn.ksb.minitxt.server.BaseServiceImpl;
import cn.ksb.minitxt.utils.NovelUtils;

public class UploadService extends BaseServiceImpl<Novel> {

	@Override
	protected void execute() throws IOException {
		//情况1:
		//文件名=小说名+".txt"
		//可能出现重名
		//必须进行重名验证
		//如果重名,要求客户端改名后再上传
		
		//情况2
		//不管小说名是什么
		//通过随机算法,生成一个不重复的文件名java.util.UUID
		//不用做重名验证,直接上传即可
		
		int result = NovelUtils.saveNovel(getData());
		DataTransfer<?> dto = new DataTransfer<>();
		dto.setResult(result);
		getOut().writeObject(dto);
		
		if(result == NovelConstants.SAVE_SUCCESS) {
			byte[] buffer = new byte[8192];
			int len = -1;
			OutputStream fos = null;
			File file = NovelUtils.makeNovelFile(getData());
			try {
				fos = new FileOutputStream(file);
				while((len = getIn().read(buffer)) != -1) {
					fos.write(buffer,0,len);
				}
				fos.flush();
			} finally {
				if(fos != null)
					fos.close();
			}
		}
	}
	
//	public static void main(String[] args) {
//		for (int i = 0; i < 20; i++) {
//			
//			UUID uuid = UUID.randomUUID();
//			System.out.println(uuid.toString());
//		}
//	}

}
