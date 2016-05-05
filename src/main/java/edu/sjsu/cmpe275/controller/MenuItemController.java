package edu.sjsu.cmpe275.controller;

import edu.sjsu.cmpe275.domain.MenuItem;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by yutao on 5/5/16.
 */
@Controller
@RequestMapping("/menuitems")
public class MenuItemController {

    @RequestMapping(method = RequestMethod.GET)
    public @ResponseBody
    List<MenuItem> getMenuItems() {
        List<MenuItem> list = new ArrayList<>();
        MenuItem item = new MenuItem();
        item.setName("ha");
        list.add(item);
        return list;
    }
}
