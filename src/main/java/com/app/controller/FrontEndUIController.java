package com.app.controller;

import javax.validation.constraints.NotBlank;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import io.swagger.v3.oas.annotations.tags.Tag;

@Controller
@Tag(name = "10.Front End Controller")
public class FrontEndUIController {
	
	@RequestMapping("/")
	public String root() {
		return "layouts/non-logged-in-layout";
	}
	
	@RequestMapping("/home")
	public String home() {
		return "layouts/logged-in-layout";
	}
	
	@RequestMapping("/page/public/{pageId}")
	public String publicPages(@PathVariable("pageId") @NotBlank(message = "pageId is mandatory") String pageId) {
		return "page/public/" + pageId;
	}
	
	@RequestMapping("/page/user-management/{pageId}")
	public String userManagement(@PathVariable("pageId") @NotBlank(message = "pageId is mandatory") String pageId) {
		return "page/user-management/" + pageId;
	}
	
	@RequestMapping("/page/admin/{pageId}")
	public String admin(@PathVariable("pageId") @NotBlank(message = "pageId is mandatory") String pageId) {
		return "page/admin/" + pageId;
	}
	
	@RequestMapping("/page/custom/{pageId}")
	public String custom(@PathVariable("pageId") @NotBlank(message = "pageId is mandatory") String pageId) {
		return "page/custom/" + pageId;
	}
}
