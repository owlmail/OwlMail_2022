package github.owlmail.contacts

import androidx.paging.PagingSource
import androidx.paging.PagingState
import github.owlmail.contacts.model.ContactRequest
import github.owlmail.contacts.model.ContactResponse
import github.owlmail.networking.ResponseState.Empty.data

class ContactPagingSource(
    private val repository: ContactRepository,
    private val searchContact: String,
    private val contactDAO: ContactDAO
) :
    PagingSource<Int, ContactResponse.Body.SearchGalResponse.Cn>() {

    override fun getRefreshKey(state: PagingState<Int, ContactResponse.Body.SearchGalResponse.Cn>): Int? {
        return null
    }

    override suspend fun load(params: PagingSource.LoadParams<Int>): PagingSource.LoadResult<Int, ContactResponse.Body.SearchGalResponse.Cn> {
        try {
            val offset = params.key ?: 0
            val loadSize = params.loadSize
            val contactRequest = ContactRequest(
                body = ContactRequest.Body(
                    searchGalRequest = ContactRequest.Body.SearchGalRequest(
                        jsns = "urn:zimbraAccount",
                        limit = loadSize,
                        offset = offset,
                        name = "$searchContact"
                    )
                )
            )
            val response = repository.getContactList(contactRequest)
            val contactList = response.body?.searchGalResponse?.cn?.filterNotNull().orEmpty()
            contactDAO.insertAllContacts(contactList)
            return PagingSource.LoadResult.Page(
                data = contactList,
                prevKey = null,
                nextKey = if (response.body?.searchGalResponse?.more == true) {
                    offset + 1
                } else null
            )
        } catch (e: Exception) {
            return PagingSource.LoadResult.Error(e)
        }
    }
}