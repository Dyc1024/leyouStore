package com.leyou.search.client;

import com.leyou.LySearchApplication;
import com.leyou.item.domain.Category;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = LySearchApplication.class)
public class CategoryClientTest {

    @Autowired
    private CategoryClient categoryClient;

    @Test
    public void testQueryCategories() {
        List<Category> names = this.categoryClient.queryCategoryByIds(Arrays.asList(1L, 2L, 3L));
        names.forEach(System.out::println);
    }
}