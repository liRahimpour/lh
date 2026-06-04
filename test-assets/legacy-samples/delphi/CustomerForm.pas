unit CustomerForm;

interface

uses
  SysUtils, Classes, Forms, Controls, StdCtrls;

type
  TCustomerForm = class(TForm)
    BtnSave: TButton;
    procedure BtnSaveClick(Sender: TObject);
  end;

implementation

procedure TCustomerForm.BtnSaveClick(Sender: TObject);
begin
  // Save customer logic will be analyzed later
end;

end.