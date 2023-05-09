const basicAuth = "Basic " + btoa("FRONT_END_UI" + ":" + "FRONT_END_UI");
const colorPalette = ['amaze', 'dark', 'lantern'];
const languages = ['en', 'ar'];

if (localStorage.getItem('token') != null && localStorage.getItem('token') != '') {
	validateAuthToken(localStorage.getItem('token'));
} else {
	if (window.location.pathname != "/") {
		window.location.href = "/";
	}
}

if (window.location.pathname === "/home") {
	$(document).ready(function(){
		buildSideBar();
	});
}

function buildSideBar() {
	$.ajax({
        type: "GET",
        url: '/api/currentUser/sideBar',
        dataType: "json",
        contentType: "application/json; charset=utf-8",
        beforeSend: function (xhr) {
            /* Authorization header */
            xhr.setRequestHeader("Authorization", "Bearer " + localStorage.getItem('token'));
            xhr.setRequestHeader("X-Mobile", "false");
        },
        success: function (data) {
        	var htmlData = "";
        	if (data.length > 0) {
        		for (var i = 0; i < data.length; i++) {
        			if (data[i].header) {
        				if (i != data.length - 1 && !data[i+1].header) 
        					htmlData = htmlData + '<div class="pl-4 text-xs mb-1 mt-4">' + data[i].name + '</div>';
        			} else if (data[i].childs === undefined) {
        				htmlData = htmlData + '<div class="pl-4 py-1 cursor-pointer hover:bg-slate-500 hover:text-white" onclick="sideBarItemActivate(this);loadPage(\'' + data[i].link + '\')">' +
 			           				'<i class="fa-solid ' + data[i].icon + '"></i>' +
 			           				'<span class="text-sm ml-2">' + data[i].name + '</span>' + 
 			           				'</div>';
        			} else {
        				htmlData = htmlData + '<div class="cursor-pointer px-4 py-1 hover:bg-slate-500 hover:text-white flex items-center"' +
        								'onclick="$(\'#' + data[i].id + '\').toggleClass(\'hidden\'); $(\'#icon-' + data[i].id + '\').toggleClass(\'rotate-90\')"">' +  
	    			            '<i class="fas ' + data[i].icon + '"></i>' + 
	    			            '<span class="text-sm ml-2">' + data[i].name + '</span>' +
	    			            '<i id="icon-' + data[i].id + '" class="fas fa-chevron-right ml-auto"></i>' +
	    			        '</div>' +
	    					'<div id="' + data[i].id + '" class="hidden" >';
        				for (var j = 0; j < data[i].childs.length; j++) {
        					
        					if (data[i].childs[j].childs === undefined) {
        						htmlData = htmlData + '<div class="pl-8 py-1 cursor-pointer hover:bg-slate-500 hover:text-white"' +
        							'onclick="sideBarItemActivate(this);loadPage(\'' + data[i].childs[j].link + '\')">' +
        							'<i class="fas ' + data[i].childs[j].icon + '"></i>'+
        							'<span class="text-sm ml-2">' + data[i].childs[j].name + '</span>'+
        							'</div>';
        					} else {
								htmlData = htmlData + '<div class="cursor-pointer px-8 py-1 hover:bg-slate-500 hover:text-white flex items-center"' + 
										'onclick="$(\'#' + data[i].childs[j].id + '\').toggleClass(\'hidden\'); $(\'#icon-' + data[i].childs[j].id + '\').toggleClass(\'rotate-90\')"">' +  
			    			            '<i class="fas ' + data[i].childs[j].icon + '"></i>' + 
			    			            '<span class="text-sm ml-2">' + data[i].childs[j].name + '</span>' +
			    			            '<i id="icon-' + data[i].childs[j].id + '" class="fas fa-chevron-right ml-auto"></i>' +
			    			        '</div>' +
			    					'<div id="' + data[i].childs[j].id + '" class="hidden" >';
        						for (var k = 0; k < data[i].childs[j].childs.length; k++) {
									htmlData = htmlData + '<div class="pl-12 py-1 cursor-pointer hover:bg-slate-500 hover:text-white"' +
	        							'onclick="sideBarItemActivate(this);loadPage(\'' + data[i].childs[j].childs[k].link + '\')">' +
	        							'<i class="fas ' + data[i].childs[j].childs[k].icon + '"></i>'+
	        							'<span class="text-sm ml-2">' + data[i].childs[j].childs[k].name + '</span>'+
	        							'</div>';
    	        				}
        						htmlData = htmlData + '</div>';
        					}
        				}
        				htmlData = htmlData + '</div>';
        			}
        		}
        	}
        	$('.sidebar-nav').html(htmlData);
        },
        error: function (data) {
            if (data.status == 401 && window.location.pathname != "/") {
            	localStorage.setItem('token','');
            	/* window.location.href = "/?logout"; */
            }
        }
   });
}

function sideBarItemActivate(current) {
	$('.sidebar-nav div.bg-slate-700').toggleClass('bg-slate-700');
	$(current).toggleClass('bg-slate-700');
}

function validateAuthToken(token) {
	if (token != null && token != "") {
		$.ajax({
	        type: "POST",
	        url: "/oauth2/verifyAuthToken?token=" + token,
	        dataType: "json",
	        contentType: "application/json; charset=utf-8",
	        beforeSend: function (xhr) {
	            /* Authorization header */
	            xhr.setRequestHeader("Authorization", basicAuth);
	            xhr.setRequestHeader("X-Mobile", "false");
	        },
	        success: function (data) {
	        	if (window.location.pathname != "/home") {
	        		window.location.href = "/home";
	        	}
	        	localStorage.setItem('firstName',data.firstName);
	        	localStorage.setItem('avatar',data.avatarImage);
	        },
	        error: function (data) {
	        	localStorage.setItem('token','');
	        	if (window.location.pathname != "/") {
	        		window.location.href = "/";
	        	}
	        }
	   });
   }
}

function logout() {
	localStorage.setItem('token','');
	window.location.href = "/";
}

function changeTitle() {
	var pageTitle = $("#page-title-value").text().toString().replaceAll('\n','').replaceAll('\t','');
	if (pageTitle != "")
		$("#page-title").text(" | " + pageTitle);
	
	$(document).attr('title', $("#main-title").text());
}


function getQueryString(paramName) {
	var queryParameters = window.location.hash.split('?')[1].split('&');
	for (let i = 0; i < queryParameters.length; i++) {
        let param = queryParameters[i].split('=', 2);
        if (param.length == 2)
            return decodeURIComponent(param[1].replace(/\+/g, " "));
    }
    return "";
}

function decodeBytes(input) {
    var output = "";
    var chr1, chr2, chr3;
    var enc1, enc2, enc3, enc4;
    var i = 0;
    input = input.replace(/[^A-Za-z0-9\+\/\=]/g, "");
    while (i < input.length) {
        enc1 = this._keyStr.indexOf(input.charAt(i++));
        enc2 = this._keyStr.indexOf(input.charAt(i++));
        enc3 = this._keyStr.indexOf(input.charAt(i++));
        enc4 = this._keyStr.indexOf(input.charAt(i++));

        chr1 = (enc1 << 2) | (enc2 >> 4);
        chr2 = ((enc2 & 15) << 4) | (enc3 >> 2);
        chr3 = ((enc3 & 3) << 6) | enc4;

        output = output + String.fromCharCode(chr1);
        if (enc3 != 64) {
            output = output + String.fromCharCode(chr2);
        }

        if (enc4 != 64) {
            output = output + String.fromCharCode(chr3);
        }
    }
    output = Base64._utf8_decode(output);
    console.log(output);
    return output;
}

function constructTableRows(enableSelection, enableUpdate, enableDelete, enableView, functionName, data, paramsArray) {
	var rows = "";
	var value;
	for (var i = 0; i < data.content.length; i++) {
		rows += "<tr class='border border-slate-400'>";
		if (enableDelete)
			rows += "<td>" + "<input type=\"checkbox\" id=\"" + data.content[i].id + "\" />" + "</td>";
		if (enableUpdate || enableDelete || enableView) {
			rows += "<td class='td-action'>";
			if (enableUpdate)
				rows += "<button type='button' class='bg-yellow-400 hover:bg-yellow-300 text-white text-md font-bold py-1 px-2 border-b-4" + 
					"border-yellow-500 hover:border-yellow-500 rounded-lg mr-1' onclick='update" + functionName + "(\"" + data.content[i].id + "\")'><i class='fas fa-pen'></i></button>" 
			if (enableDelete)
				rows += "<button type='button' class='bg-red-600 hover:bg-red-500 text-white text-md font-bold py-1 px-2 border-b-4" + 
					"border-red-800 hover:border-red-700 rounded-lg mr-1' onclick='delete" + functionName + "(\"" + data.content[i].id + "\")'><i class='fas fa-trash-alt'></i></button>"
			if (enableView)
				rows += "<button type='button' class='bg-primary-600 hover:bg-primary-500 text-white text-md font-bold py-1 px-2 border-b-4" + 
					"border-primary-800 hover:border-primary-700 mr-1' onclick='view" + functionName + "(\"" + data.content[i].id + "\")'><i class='fas fa-eye'></i></button>"
							
			rows += "</td>";
		}
		for (var j = 0; j < paramsArray.length; j++) {
			value = data.content[i][paramsArray[j]] === undefined ? "" : data.content[i][paramsArray[j]];
			rows += "<td>" + value + "</td>";
		}
		rows += "</tr>";
	}
	return rows;
}

function constructPagination(parentId, pageSize, pageNo, data) {
	var pageNum = parseInt(pageNo);
	$('#' + parentId + ' #startIndex').text((pageNum - 1) * pageSize + 1);
	$('#' + parentId + ' #endIndex').text((pageNum-1) * pageSize + data.numberOfElements);
	$('#' + parentId + ' #totalCount').text(data.totalElements);
	
	//prev-2
	if (pageNum - 2 > 0) {
		$('#' + parentId + ' #prev-2').removeClass('hidden');
		$('#' + parentId + ' #prev-2').text(pageNum - 2);
	} else {
		$('#' + parentId + ' #prev-2').addClass('hidden');
		$('#' + parentId + ' #prev-2').text(1);
	}
	
	//prev-1
	if (pageNum - 1 > 0) {
		$('#' + parentId + ' #prev-1').removeClass('hidden');
		$('#' + parentId + ' #prev-1').text(pageNum - 1);
	} else {
		$('#' + parentId + ' #prev-1').addClass('hidden');
		$('#' + parentId + ' #prev-1').text(1);
	}
	
	//next-1
	if (pageNum + 1 <= data.totalPages) {
		$('#' + parentId + ' #next-1').removeClass('hidden');
		$('#' + parentId + ' #next-1').text(pageNum + 1);
	} else {
		$('#' + parentId + ' #next-1').addClass('hidden');
		$('#' + parentId + ' #next-1').text(data.totalPages);
	}
	
	//next-2
	if (pageNum + 2 <= data.totalPages) {
		$('#' + parentId + ' #next-2').removeClass('hidden');
		$('#' + parentId + ' #next-2').text(pageNum + 2);
	} else {
		$('#' + parentId + ' #next-2').addClass('hidden');
		$('#' + parentId + ' #next-2').text(data.totalPages);
	}
	
	//last
	$('#' + parentId + ' #pag-last-value').text(data.totalPages);
}

function loadPage(name, queryString) {
	validateAuthToken(localStorage.getItem('token'));
	
	if (name.startsWith('#')) {
		name = name.substr(1);
	}
	
	const jsUrl = 'js' + (name.includes('?') ? name.split('?')[0] : name) + '.js';
	window.location.hash=name;
	$.ajax({
        type: "GET",
        url: '/page' + name,
        dataType: "html",
        contentType: "application/json; charset=utf-8",
        beforeSend: function (xhr) {
            /* Authorization header */
            xhr.setRequestHeader("Authorization", basicAuth);
            xhr.setRequestHeader("X-Mobile", "false");
        },
        success: function (data) {
        	$('#page-wrapper').html(data);
        	changeTitle();
        	$('#mainModal').html($('.modal-container').html());
        	$.getScript(jsUrl).done(function(script, textStatus) {
        		if (queryString != undefined) 
        			window.history.pushState({}, document.title, window.location.pathname + window.location.hash + queryString);
        		else
        			window.history.pushState({}, document.title, window.location.pathname + window.location.hash);
        			
        		init();
        	});
        	
        },
        error: function (data) {
            if (data.status == 401 && window.location.pathname != "/") {
            	localStorage.setItem('token','');
            	window.location.href = "/?logout";
            }
        }
   });
}


function displayDeletePopup(clickFunction) {
	$('#deletePopup #deleteConfirmButton').off('click');
	$('#deletePopup #deleteConfirmButton').click(clickFunction);
	$('#deletePopup').toggleClass('hidden');
}

function displayAlertPopup(icon, iconColor, message, clickFunction) {
	$('#alertPopup .modal-header').html('<i class="fas fa-5x ' + icon + ' ' + iconColor + '"></i>');
	$('#alertPopup #alertMessage').html(message);
	$('#alertPopup .modal-footer button').off('click');
	$('#alertPopup .modal-footer button').click(clickFunction);
	$("#alertPopup").toggleClass('hidden');
}

function handleApplicationError(data) {
	if (data.status === 401) {
		localStorage.setItem('token','');
		if (window.location.pathname != "/") {
			window.location.href = "/";
			displayAlertPopup('fa-times-circle', 'text-red-600', 'You have been logged out.', function() {console.log(errorMessage);$("#alertPopup").toggleClass('hidden');});
		} else {
			displayAlertPopup('fa-times-circle', 'text-red-600', 'Invalid credentials.', function() {console.log(errorMessage);$("#alertPopup").toggleClass('hidden');});
		}
	} else if (data.status === 403) {
		displayAlertPopup('fa-skull-crossbones', 'text-red-600', 'Unauthorized access.', function() {console.log(errorMessage);$("#alertPopup").toggleClass('hidden');});
	} else {
		$('#alertPopup .modal-header').html('<i class="fas fa-5x fa-times-circle text-danger"></i>');
		var errorMessage = data.responseJSON.message;
		if (data.responseJSON.error !== undefined && data.responseJSON.error !== "" && data.responseJSON.error.length > 0) {
			for (var i = 0; i < data.responseJSON.error.length; i++) {
				errorMessage = data.responseJSON.error[i].errorCode + " : " + data.responseJSON.error[i].errorMessage + "<br/>"  
			}
		}
		displayAlertPopup('fa-times-circle', 'text-red-600', errorMessage, function() {console.log(errorMessage);$("#alertPopup").toggleClass('hidden');});
	}
}

function getTimeZoneOffset() {
    var t = new Date().toString().match(/[-\+]\d{4}/)[0];
    return ":00.000" + t.substring(0,3) + ":" + t.substr(3);
}

function getFormData(formElement) {
	var rawJson = formElement.serializeArray();
	var model = {};

	$.map(rawJson, function (n, i) {
		if (n['value'] !== "" && (formElement.find(":input[name=" + n['name'] + "]").attr('type') === "number" 
			|| formElement.find(":input[name=" + n['name'] + "]").attr('type') === "tel"))
			model[n['name']] = Number(n['value']);
		else if (n['value'] !== "" && (formElement.find(":input[name=" + n['name'] + "]").attr('type') === "datetime-local"))
			model[n['name']] = n['value'].slice(0,16).concat(getTimeZoneOffset());
		else
			model[n['name']] = n['value'];
	});
	
	formElement.find('input[type=checkbox]:not(:checked)').map(function(){
		model[this.name] = this.title;
	});

	return model;
}


function serializeFormData(formName) {
	var formSerData = $("form[name=" + formName + "]").serialize().split("&");
	var searchData = "";
	var keyValue;
	for (var i=0; i<formSerData.length; i++) {
		keyValue = formSerData[i].split("=");
		if ($("form[name=" + formName + "] input[name=" + keyValue[0] + "]").attr("type") == "datetime-local"
				&& keyValue[1] !== undefined && keyValue[1] !== "") {
			searchData += keyValue[0] + "=" + keyValue[1] + encodeURIComponent(getTimeZoneOffset()) + "&";
		} else {
			searchData += keyValue[0] + "=" + keyValue[1] + "&";
		}
	}
	return searchData;
}

function loadFormData(formElement, data) {
	$.map(data, function (v, k) {
		if (formElement.find(":input[name=" + k + "]").attr('type') !== undefined) {
			if (formElement.find(":input[name=" + k + "]").attr('type') == "datetime-local")
				formElement.find(":input[name=" + k + "]").val(v.slice(0,19));
			else if (formElement.find(":input[name=" + k + "]").attr('type') == "checkbox")
				formElement.find(":input[name=" + k + "]").prop('checked', v);
			else
				formElement.find(":input[name=" + k + "]").val(v);
		}
		if (formElement.find("select[name=" + k + "]").attr('name') !== undefined) {
			formElement.find("select[name=" + k + "]").val(v.toString());
		}
	});
}

/*function  getObjectFormFields(form) {

    var result = {};
    var arrayAuxiliar = [];
    form.find(":input:text").each(function (index, element) {
        result[$(element).attr('name')] = $(element).val();
    });
    form.find(":input[type=hidden]").each(function (index, element) {
    	if ($(element).attr('name') === 'mobileNumberCountryCode')
    		result[$(element).attr('name')] = Number($(element).val());
    	else 
    		result[$(element).attr('name')] = $(element).val();
    });
    form.find(":input[type=number]").each(function (index, element) {
    	result[$(element).attr('name')] = Number($(element).val());
    });
    form.find(":input[type=tel]").each(function (index, element) {
    	result[$(element).attr('name')] = Number($(element).val());
    });
    form.find(":input:checked").each(function (index, element) {
    	var name;
        var value;
        if ($(this).attr("type") == "radio") {
        	result[$(element).attr('name')] = Number($(element).val());
        }
        else if ($(this).attr("type") == "checkbox") {
            name = $(element).attr('name');
            value = $(element).val();
            if (result[name])
            {
                if (Array.isArray(result[name]))
                {
                    result[name].push(value);
                } else
                {
                    var aux = result[name];
                    result[name] = [];
                    result[name].push(aux);
                    result[name].push(value);
                }

            } else
            {
                result[name] = [];
                result[name].push(value);
            }
        }

    });
    form.find("select option:selected").each(function (index, element) {
        result[$(element).parent().attr('name')] = $(element).val();
    });

    arrayAuxiliar = [];
    form.find("checkbox:checked").each(function (index, element)
    {
        var name = $(element).attr('name');
        var value = $(element).val();
        result[name] = arrayAuxiliar.push(value);
    });

    form.find("textarea").each(function (index, element)
    {
        var name = $(element).attr('name');
        var value = $(element).val();
        result[name] = value;
    });

    return result;
}*/

jQuery.validator.setDefaults({
    onfocusout: function (e) {
        this.element(e);
    },
    onkeyup: false,

    highlight: function (element) {
        jQuery(element).addClass('border-red-500');
    },
    unhighlight: function (element) {
        jQuery(element).removeClass('border-red-500');
        jQuery(element).addClass('border-green-600');
    },

    errorElement: 'div',
    errorClass: 'invalid-tooltip',
    errorPlacement: function (error, element) {
        error.insertAfter(element);
    },
});

/*$.validator.addMethod("email", function(value, element) {
	return this.optional(element) || /^.+@goli.pk$/.test(value);
},"Only company emails are allowed.");*/

$.validator.addMethod("checkLower", function(value) {
  return /[a-z]/.test(value);
},"Password must contain atleast 1 lower case letter");
$.validator.addMethod("checkUpper", function(value) {
  return /[A-Z]/.test(value);
},"Password must contain atleast 1 upper case letter");
$.validator.addMethod("checkDigit", function(value) {
  return /[0-9]/.test(value);
},"Password must contain atleast 1 lower case letter");
$.validator.addMethod("checkSpecial", function	(value) {
  return /[*@#$&_\-]/.test(value);
},"Password must contain atleast 1 special letter(*,@,#,$,&,_,-)");
