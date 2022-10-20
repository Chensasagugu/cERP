import com.jsh.erp.ErpApplication;
import com.jsh.erp.domin.service.LoadDustoService;
import com.jsh.erp.thirdparty.tableocr.TableOCRUtils;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.io.File;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {ErpApplication.class})
public class Test {
    @Resource
    LoadDustoService loadDustoService;


    @org.junit.Test
    public void test(){
        File excel = new File("/Users/chen/Documents/9-6.xls");
        loadDustoService.enterOrder(excel);
    }

    @org.junit.Test
    public void testLoadImage(){
        String imageUrl = "/Users/chen/Desktop/WechatIMG541.png";
        loadDustoService.generateOutboundOrder(imageUrl);
    }
}
