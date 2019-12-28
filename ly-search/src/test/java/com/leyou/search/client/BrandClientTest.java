package com.leyou.search.client;

import com.leyou.item.domain.Brand;
import junit.framework.TestCase;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class BrandClientTest extends TestCase {
    @Autowired
    private BrandClient brandClient;

    @Test
    public void queryBrand(){
        Brand brand = brandClient.queryBrandById(1912L);
        Assert.assertEquals("1912",brand.getId());
        System.out.println(brand);
    }
}