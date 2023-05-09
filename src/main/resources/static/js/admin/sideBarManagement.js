function init() {
	changeTitle();
}

function handleDownloadSideBar() {
	
	$('#loader1').show(0);
	$.ajax({
		type : "GET",
		url : "/api/sidebar/extract?" + $("form[name=searchSideBarForm]").serialize(),
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
		    link.download="SideBars_" + new Date().toLocaleString().replace(',','').replaceAll('/','-').replace(' ','T').replace(' ','') + ".csv";
		    link.click();
		},
		error : function(data) {
			$('#loader1').hide(0);
			handleApplicationError(data);
		}
	});
}

function handleSearchSideBar() {
	
	var searchData = "pageSize=" + $("#sideBarTableSection #pageSize").val() + "&pageNo=" + $("#sideBarTableSection #pageNo").text() + "&";
	$('#loader1').show(0);
	$.ajax({
		type : "GET",
		url : "/api/sidebar?" + searchData + $("form[name=searchSideBarForm]").serialize(),
		dataType : "json",
		contentType : "application/json; charset=utf-8",
		beforeSend : function(xhr) {
			xhr.setRequestHeader("Authorization", "Bearer "
					+ localStorage.getItem('token'));
			xhr.setRequestHeader("X-Mobile", "false");
		},
		success : function(data) {
			$('#loader1').hide(0);
			if ($('#sideBarTableSection').hasClass('hidden')) {
				$('#sideBarTableSection').toggleClass('hidden');
			}
			$('#sideBarTableSection table tbody').html('');
			if (data.content.length > 0) {
				$('#sideBarTableSection #selectAll').prop('checked',false);
				var paramsArray = ["name","icon","parentId","link","sequence","authorities","enabled","header","root"];
				$('#sideBarTableSection table tbody').html(constructTableRows(true, true, true, false, 'SideBar', data, paramsArray));
				constructPagination('sideBarTableSection', $("#sideBarTableSection #pageSize").val(), $("#sideBarTableSection #pageNo").text(), data);
			}
		},
		error : function(data) {
			$('#loader1').hide(0);
			handleApplicationError(data);
		}
	});
}


function deleteSideBar(id) {
	displayDeletePopup(function() {
		$('#deletePopup').toggleClass('hidden');
		$('#loader1').show(0);
		$.ajax({
			type : "DELETE",
			url : "/api/sidebar/" + id,
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
					handleSearchSideBar();
				});
			},
			error : function(data) {
				$('#loader1').hide(0);
				handleApplicationError(data);
			}
		});
	});
}

function updateSideBar(id) {
	$('#loader').show(0);
	$.ajax({
		type : "GET",
		url : "/api/sidebar/" + id,
		dataType : "json",
		contentType : "application/json; charset=utf-8",
		beforeSend : function(xhr) {
			xhr.setRequestHeader("Authorization", "Bearer "
					+ localStorage.getItem('token'));
			xhr.setRequestHeader("X-Mobile", "false");
		},
		success : function(data) {
			$('#loader').hide(0);
			$('#mainModal').html($('.update-sidebar-modal-container').html());
			$('#mainPopup').toggleClass('hidden');
			loadFormData($('#mainModal form[name=updateSideBarForm]'), data);
		},
		error : function(data) {
			$('#loader').hide(0);
			$('#mainPopup').toggleClass('hidden');
			handleApplicationError(data);
		}
	});
}

function handleUpdateSideBar() {
	$('#loader').show(0);
	
	var formData = getFormData($("#mainModal form[name=updateSideBarForm]"));
	
	$.ajax({
		type : "PUT",
		url : "/api/sidebar/" + formData.id,
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
					handleSearchSideBar();
				});
		},
		error : function(data) {
			$('#loader').hide(0);
			$('#mainPopup').toggleClass('hidden');
			handleApplicationError(data);
		}
	});
}

function createSideBar() {
	$('#loader').show(0);
	$('#mainModal').html($('.create-sidebar-modal-container').html());
	$('#mainPopup').toggleClass('hidden');
	$('#loader').hide(0);
}

function handleCreateSideBar() {
	$('#loader').show(0);
	$.ajax({
		type : "POST",
		url : "/api/sidebar/",
		data: JSON.stringify(getFormData($("#mainModal form[name=createSideBarForm]"))),
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
				});
		},
		error : function(data) {
			$('#loader').hide(0);
			handleApplicationError(data);
		}
	});
}

