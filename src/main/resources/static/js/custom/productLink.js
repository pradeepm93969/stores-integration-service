function init() {
	changeTitle();
}

function handleDownloadProductLink() {
	
	$('#loader1').show(0);
	$.ajax({
		type : "GET",
		url : "/api/productLinks/extract?" + serializeFormData("searchProductLinkForm"),
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
		    link.download="ProductLink_" + new Date().toLocaleString().replace(',','').replaceAll('/','-').replace(' ','T').replace(' ','') + ".csv";
		    link.click();
		},
		error : function(data) {
			$('#loader1').hide(0);
			handleApplicationError(data);
		}
	});
}

function handleSearchProductLink() {
	
	var searchData = "pageSize=" + $("#productLinkTableSection #pageSize").val() + "&pageNo=" + $("#productLinkTableSection #pageNo").text() + "&";
	$('#loader1').show(0);
	$.ajax({
		type : "GET",
		url : "/api/productLinks?" + searchData + serializeFormData("searchProductLinkForm"),
		dataType : "json",
		contentType : "application/json; charset=utf-8",
		beforeSend : function(xhr) {
			xhr.setRequestHeader("Authorization", "Bearer "
					+ localStorage.getItem('token'));
			xhr.setRequestHeader("X-Mobile", "false");
		},
		success : function(data) {
			$('#loader1').hide(0);
			if ($('#productLinkTableSection').hasClass('hidden')) {
				$('#productLinkTableSection').toggleClass('hidden');
			}
			$('#productLinkTableSection table tbody').html('');
			if (data.content.length > 0) {
				$('#productLinkTableSection #selectAll').prop('checked',false);
				var paramsArray = ["id","productId","vendorProductId","storeId","enabled","listPrice","salePrice","inventory"];
				$('#productLinkTableSection table tbody').html(constructTableRows(true, true, true, false, 'ProductLink', data, paramsArray));
				constructPagination('productLinkTableSection', $("#productLinkTableSection #pageSize").val(), $("#productLinkTableSection #pageNo").text(), data);
			}
		},
		error : function(data) {
			$('#loader1').hide(0);
			handleApplicationError(data);
		}
	});
}

function handleUploadProductLink() {
	
	$('#loader').show(0);
	let formData = new FormData(); 
  	formData.append('file', $("form[name=uploadProductLinkForm]").find(':input')[0].files[0]);
	$.ajax({
		type : "POST",
		url : "/api/productLinks/upload",
		data: formData,
		processData: false,  // tell jQuery not to process the data
        contentType: false,  // tell jQuery not to set contentType
		beforeSend : function(xhr) {
			xhr.setRequestHeader("Authorization", "Bearer "
					+ localStorage.getItem('token'));
			xhr.setRequestHeader("X-Mobile", "false");
		},
		success : function(data) {
			$('#loader').hide(0);
			displayAlertPopup('fa-check-circle', 'text-green-600', 'Successfully Uploaded...', function() {
				$('#alertPopup').toggleClass('hidden');
			});
		},
		error : function(data) {
			$('#loader').hide(0);
			handleApplicationError(data);
		}
	});
}


function deleteProductLink(id) {
	displayDeletePopup(function() {
		$('#deletePopup').toggleClass('hidden');
		$('#loader1').show(0);
		$.ajax({
			type : "DELETE",
			url : "/api/productLinks/" + id,
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
					handleSearchProductLink();
				});
			},
			error : function(data) {
				$('#loader1').hide(0);
				handleApplicationError(data);
			}
		});
	});
}

function updateProductLink(id) {
	$('#loader').show(0);
	$.ajax({
		type : "GET",
		url : "/api/productLinks/" + id,
		dataType : "json",
		contentType : "application/json; charset=utf-8",
		beforeSend : function(xhr) {
			xhr.setRequestHeader("Authorization", "Bearer "
					+ localStorage.getItem('token'));
			xhr.setRequestHeader("X-Mobile", "false");
		},
		success : function(data) {
			$('#loader').hide(0);
			$('#mainModal').html($('.update-productLink-modal-container').html());
			$('#mainPopup').toggleClass('hidden');
			loadFormData($('#mainModal form[name=updateProductLinkForm]'), data);
		},
		error : function(data) {
			$('#loader').hide(0);
			$('#mainPopup').toggleClass('hidden');
			handleApplicationError(data);
		}
	});
}

function handleUpdateProductLink() {
	$('#loader').show(0);
	
	var formData = getFormData($("#mainModal form[name=updateProductLinkForm]"));
	
	$.ajax({
		type : "PUT",
		url : "/api/productLinks/" + formData.id,
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
				handleSearchProductLink();
			});
		},
		error : function(data) {
			$('#loader').hide(0);
			$('#mainPopup').toggleClass('hidden');
			handleApplicationError(data);
		}
	});
}

function createProductLink() {
	$('#loader').show(0);
	$('#mainModal').html($('.create-productLink-modal-container').html());
	$('#mainPopup').toggleClass('hidden');
	$('#loader').hide(0);
}

function handleCreateProductLink() {
	$('#loader').show(0);
	$.ajax({
		type : "POST",
		url : "/api/productLinks",
		data: JSON.stringify(getFormData($("#mainModal form[name=createProductLinkForm]"))),
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

