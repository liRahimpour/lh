object CustomerForm: TCustomerForm
  object BtnSave: TButton
    Caption = 'Save'
    OnClick = BtnSaveClick
  end
  object EditName: TEdit
    Text = ''
    OnChange = EditNameChange
  end
end