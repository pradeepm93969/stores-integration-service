function init() {
	changeTitle();
}

function handleDownloadRole() {
	
	$('#loader1').show(0);
	$.ajax({
		type : "GET",
		url : "/api/role/extract?" + $("form[name=searchRoleForm]").serialize(),
		beforeSend : function(xhr) {
			/* Authorization header */
			xhr.setRequestHeader("Authorization", "Bearer "
					+ localStorage.getItem('token'));
			xhr.setRequestHeader("X-Mobile", "false");
		},
		success : function(data) {
			$('#loader1').hide(0);
			var blob=new Blob([data]);
		    var link=document.createElement('a');
		    link.href=window.URL.createObjectURL(blob);
		    link.download="Role_" + new Date().toLocaleString().replace(',','').replaceAll('/','-').replace(' ','T').replace(' ','') + ".csv";
		    link.click();
		},
		error : function(data) {
			$('#loader1').hide(0);
			handleApplicationError(data);
		}
	});
}

function handleSearchRole() {
	
	var searchData = "pageSize=" + $("#roleTableSection #pageSize").val() + "&pageNo=" + $("#roleTableSection #pageNo").text() + "&";
	$('#loader1').show(0);
	$.ajax({
		type : "GET",
		url : "/api/role?" + searchData + $("form[name=searchRoleForm]").serialize(),
		dataType : "json",
		contentType : "application/json; charset=utf-8",
		beforeSend : function(xhr) {
			xhr.setRequestHeader("Authorization", "Bearer "
					+ localStorage.getItem('token'));
			xhr.setRequestHeader("X-Mobile", "false");
		},
		success : function(data) {
			$('#loader1').hide(0);
			if ($('#roleTableSection').hasClass('hidden')) {
				$('#roleTableSection').toggleClass('hidden');
			}
			$('#roleTableSection table tbody').html('');
			if (data.content.length > 0) {
				$('#roleTableSection #selectAll').prop('checked',false);
				var paramsArray = ["id","name"];
				$('#roleTableSection table tbody').html(constructTableRows(true, true, true, false, 'Role', data, paramsArray));
				constructPagination('roleTableSection', $("#roleTableSection #pageSize").val(), $("#roleTableSection #pageNo").text(), data);
			}
		},
		error : function(data) {
			$('#loader1').hide(0);
			handleApplicationError(data);
		}
	});
}


function deleteRole(id) {
	displayDeletePopup(function() {
		$('#deletePopup').toggleClass('hidden');
		$('#loader1').show(0);
		$.ajax({
			type : "DELETE",
			url : "/api/role/" + id,
			dataType : "json",
			contentType : "application/json; charset=utf-8",
			beforeSend : function(xhr) {
				xhr.setRequestHeader("Authorization", "Bearer "
						+ localStorage.getItem('token'));
				xhr.setRequestHeader("X-Mobile", "false");
			},
			success : function(data) {
				$('#loader1').hide(0);
				displayAlertPopup('fa-check-circle', 'text-green-600', 'Successfully deleted...', function() {
					$('#alertPopup').toggleClass('hidden');
					handleSearchRole();
				});
			},
			error : function(data) {
				$('#loader1').hide(0);
				handleApplicationError(data);
			}
		});
	});
}

function updateRole(id) {
	$('#loader').show(0);
	$.ajax({
		type : "GET",
		url : "/api/role/" + id,
		dataType : "json",
		contentType : "application/json; charset=utf-8",
		beforeSend : function(xhr) {
			xhr.setRequestHeader("Authorization", "Bearer "
					+ localStorage.getItem('token'));
			xhr.setRequestHeader("X-Mobile", "false");
		},
		success : function(data) {
			$('#mainModal').html($('.update-role-modal-container').html());
			$('#mainPopup').toggleClass('hidden');
			
			var html = "";
			var childHtml = "";
			var checked = "";
			var isAllChecked = false;
			var selectAllClick = 'onclick="$(\'.card-body input:checkbox\', $(this).closest(\'.card\')).prop(\'checked\',this.checked);"';
			for (const [key, value] of Object.entries(data.permissionsMap)) {
				childHtml = "";
				isAllChecked = true;
				for (var i = 0; i < value.length; i++) {
					childHtml += '<div class="' + value[i].name + '">';
					checked = (value[i].enabled) ? "checked" : "";
					childHtml += '<input class="cursor-pointer" type="checkbox" id="' + value[i].id + '" ' + checked +'/>';
					childHtml += '<span class="ml-2">' + value[i].name + '</span>';
					childHtml += '</div>';
					isAllChecked = isAllChecked & value[i].enabled;
				}
				
				html += '<div class="card card flex flex-col shadow-lg rounded-lg bg-slate-200 border-1">';
				html += '<div class="card-header rounded-t bg-slate-300 text-slate-900 mb-0 p-2 text-base font-semibold">';
				html += '<input class="cursor-pointer"  type="checkbox" id="' + key + '" ' + selectAllClick + ' ' + (isAllChecked === 1 ? "checked" : "") + ' />';
				html += '<span class="ml-2">' + key + '</span>';
				html += '</div>';
				html += '<div class="card-body p-2">';
				html += childHtml;
				html += '</div>';
				html += '</div>';
				html += '</div>';
			}
			
			$('#mainModal #permissionsMap').html(html);
			loadFormData($('#mainModal form[name=updateRoleForm]'), data);
			$('#loader').hide(0);
		},
		error : function(data) {
			$('#loader').hide(0);
			handleApplicationError(data);
		}
	});
}

function handleUpdateRole() {
	$('#loader').show(0);
	
	var formData = {
			'id' : $("#mainModal form[name=updateRoleForm] input[name=id]").val(),
			'name' : $("#mainModal form[name=updateRoleForm] input[name=name]").val(),
			'permissionsMap' : buildPermissionMap($("#mainModal form[name=updateRoleForm]"))
		}
	
	$.ajax({
		type : "PUT",
		url : "/api/role/" + $("#mainModal form[name=updateRoleForm] input[name=id]").val(),
		data: JSON.stringify(formData),
		dataType : "json",
		contentType : "application/json; charset=utf-8",
		beforeSend : function(xhr) {
			xhr.setRequestHeader("Authorization", "Bearer "
					+ localStorage.getItem('token'));
			xhr.setRequestHeader("X-Mobile", "false");
		},
		success : function(data) {
			$('#loader').hide(0);
			displayAlertPopup('fa-check-circle', 'text-green-600', 'Successfully updated...', function() {
				$('#alertPopup').toggleClass('hidden');
				handleSearchRole();
			});
		},
		error : function(data) {
			$('#loader').hide(0);
			handleApplicationError(data);
		}
	});
}

function createRole() {
	$('#loader').show(0);
	$('#mainModal').html($('.create-role-modal-container').html());
	$('#mainPopup').toggleClass('hidden');
	
	//first role is admin and there is no permissions associated with that 
	$.ajax({
		type : "GET",
		url : "/api/role/ROLE_ADMIN",
		dataType : "json",
		contentType : "application/json; charset=utf-8",
		beforeSend : function(xhr) {
			xhr.setRequestHeader("Authorization", "Bearer "
					+ localStorage.getItem('token'));
			xhr.setRequestHeader("X-Mobile", "false");
		},
		success : function(data) {
			$('#loader').hide(0);
			var html = "";
			var selectAllClick = 'onclick="$(\'.card-body input:checkbox\', $(this).closest(\'.card\')).prop(\'checked\',this.checked);"';
			for (const [key, value] of Object.entries(data.permissionsMap)) {
				html += '<div class="card flex flex-col shadow-lg rounded-lg bg-slate-200 border-1">';
				html += '<div class="card-header rounded-t bg-slate-300 text-slate-900 mb-0 p-2 text-base font-semibold">';
				html += "<input class='cursor-pointer' type='checkbox' id='" + key + "' " + selectAllClick + " />";
				html += '<span class="ml-2">' + key + '</span>';
				html += '</div>';
				html += '<div class="card-body p-2">';
				for (var i = 0; i < value.length; i++) {
					html += '<div class="' + value[i].name + '">';
					html += '<input class="cursor-pointer" type="checkbox" id="' + value[i].id + '"/>';
					html += '<span class="ml-2">' + value[i].name + '</span>';
					html += '</div>';
				}
				html += '</div>';
				html += '</div>';
				html += '</div>';
			}
			
			$('#mainModal #permissionsMap').html(html);
			$('#loader').hide(0);
		},
		error : function(data) {
			$('#loader').hide(0);
			handleApplicationError(data);
		}
	});
	
	$('#loader').hide(0);
}

function handleCreateRole() {
	$('#loader').show(0);
	
	var data = {
		'id' : $("#mainModal form[name=createRoleForm] input[name=id]").val(),
		'name' : $("#mainModal form[name=createRoleForm] input[name=name]").val(),
		'permissionsMap' : buildPermissionMap($("#mainModal form[name=createRoleForm]"))
	}
	
	$.ajax({
		type : "POST",
		url : "/api/role/",
		data: JSON.stringify(data),
		dataType : "json",
		contentType : "application/json; charset=utf-8",
		beforeSend : function(xhr) {
			xhr.setRequestHeader("Authorization", "Bearer "
					+ localStorage.getItem('token'));
			xhr.setRequestHeader("X-Mobile", "false");
		},
		success : function(data) {
			$('#loader').hide(0);
			displayAlertPopup('fa-check-circle', 'text-green-600', 'Successfully created...', function() {
				$('#alertPopup').toggleClass('hidden');
				handleSearchRole();
			});
		},
		error : function(data) {
			$('#loader').hide(0);
			handleApplicationError(data);
		}
	});
}

function buildPermissionMap(form) {
	var permissions = {}
	var cards = $(form).find('.card');
	var card = null;
	var childs = [];
	var input = null;
	for (var i = 0; i < cards.length; i++) {
		card = cards[i];
		childs = [];
		for (var j = 0; j < $(card).find('.card-body input').length; j++) {
			input = $(card).find('.card-body input')[j];
			childs[j] = {
				'enabled':  $(input).is(':checked'),
				'id': $(input).attr('id')
			};
		}
		permissions[$(card).find('.card-header span').html()] = childs;
	}
	return permissions;
}
