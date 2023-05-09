function init() {
	changeTitle();
}

function handleDownloadStore() {
	
	$('#loader1').show(0);
	$.ajax({
		type : "GET",
		url : "/api/stores/extract?" + serializeFormData("searchStoreForm"),
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
		    link.download="Store_" + new Date().toLocaleString().replace(',','').replaceAll('/','-').replace(' ','T').replace(' ','') + ".csv";
		    link.click();
		},
		error : function(data) {
			$('#loader1').hide(0);
			handleApplicationError(data);
		}
	});
}

function handleSearchStore() {
	
	var searchData = "pageSize=" + $("#storeTableSection #pageSize").val() + "&pageNo=" + $("#storeTableSection #pageNo").text() + "&";
	$('#loader1').show(0);
	$.ajax({
		type : "GET",
		url : "/api/stores?" + searchData + serializeFormData("searchStoreForm"),
		dataType : "json",
		contentType : "application/json; charset=utf-8",
		beforeSend : function(xhr) {
			xhr.setRequestHeader("Authorization", "Bearer "
					+ localStorage.getItem('token'));
			xhr.setRequestHeader("X-Mobile", "false");
		},
		success : function(data) {
			$('#loader1').hide(0);
			if ($('#storeTableSection').hasClass('hidden')) {
				$('#storeTableSection').toggleClass('hidden');
			}
			$('#storeTableSection table tbody').html('');
			if (data.content.length > 0) {
				$('#storeTableSection #selectAll').prop('checked',false);
				var paramsArray = ["id","name","enabled","vendorName","vendorStoreId","host","username","password","productPriceSyncEnabled","productPriceSyncStatus","lastProductPriceSyncAt","inventorySyncEnabled","inventorySyncStatus","lastInventorySyncAt"];
				$('#storeTableSection table tbody').html(constructTableRows(true, true, true, false, 'Store', data, paramsArray));
				constructPagination('storeTableSection', $("#storeTableSection #pageSize").val(), $("#storeTableSection #pageNo").text(), data);
			}
		},
		error : function(data) {
			$('#loader1').hide(0);
			handleApplicationError(data);
		}
	});
}


function deleteStore(id) {
	displayDeletePopup(function() {
		$('#deletePopup').toggleClass('hidden');
		$('#loader1').show(0);
		$.ajax({
			type : "DELETE",
			url : "/api/stores/" + id,
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
					handleSearchStore();
				});
			},
			error : function(data) {
				$('#loader1').hide(0);
				handleApplicationError(data);
			}
		});
	});
}

function updateStore(id) {
	$('#loader').show(0);
	$.ajax({
		type : "GET",
		url : "/api/stores/" + id,
		dataType : "json",
		contentType : "application/json; charset=utf-8",
		beforeSend : function(xhr) {
			xhr.setRequestHeader("Authorization", "Bearer "
					+ localStorage.getItem('token'));
			xhr.setRequestHeader("X-Mobile", "false");
		},
		success : function(data) {
			$('#loader').hide(0);
			$('#mainModal').html($('.update-store-modal-container').html());
			$('#mainPopup').toggleClass('hidden');
			loadFormData($('#mainModal form[name=updateStoreForm]'), data);
		},
		error : function(data) {
			$('#loader').hide(0);
			$('#mainPopup').toggleClass('hidden');
			handleApplicationError(data);
		}
	});
}

function handleUpdateStore() {
	$('#loader').show(0);
	
	var formData = getFormData($("#mainModal form[name=updateStoreForm]"));
	
	$.ajax({
		type : "PUT",
		url : "/api/stores/" + formData.id,
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
				handleSearchStore();
			});
		},
		error : function(data) {
			$('#loader').hide(0);
			$('#mainPopup').toggleClass('hidden');
			handleApplicationError(data);
		}
	});
}

function createStore() {
	$('#loader').show(0);
	$('#mainModal').html($('.create-store-modal-container').html());
	$('#mainPopup').toggleClass('hidden');
	$('#loader').hide(0);
}

function handleCreateStore() {
	$('#loader').show(0);
	$.ajax({
		type : "POST",
		url : "/api/stores",
		data: JSON.stringify(getFormData($("#mainModal form[name=createStoreForm]"))),
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

function handleTableAction() {
	$('#loader').show(0);
	var actionName = $('#actionName').val();
	if (actionName == "") {
		$('#loader').hide(0);
		return;
	}
	var dataArray = [];
	$('#storeTableSection td input:checkbox:checked').each(function () {
    	dataArray.push($(this).attr('id'));
	});
	
	$.ajax({
		type : "POST",
		url : "/api/stores/" +  actionName,
		data: JSON.stringify(dataArray),
		contentType : "application/json; charset=utf-8",
		beforeSend : function(xhr) {
			xhr.setRequestHeader("Authorization", "Bearer "
					+ localStorage.getItem('token'));
			xhr.setRequestHeader("X-Mobile", "false");
		},
		success : function(data) {
			$('#loader').hide(0);
			
			displayAlertPopup('fa-check-circle', 'text-green-600', 'Sync has been initiated...', function() {
				$('#alertPopup').toggleClass('hidden');
			});
		},
		error : function(data) {
			$('#loader').hide(0);
			handleApplicationError(data);
		}
	});
}
