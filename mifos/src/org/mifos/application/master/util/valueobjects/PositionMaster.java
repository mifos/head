
   package org.mifos.application.master.util.valueobjects;


    public class PositionMaster
    {


        private java.lang.Integer positionId;


        private java.lang.String positionName;


	    public PositionMaster()
	    {
		}
		public PositionMaster(java.lang.Integer positionId,java.lang.String positionName)
		{

			this.positionId = positionId;
			this.positionName = positionName;
		}

        public void setPositionId(java.lang.Integer positionId)
        {
             this.positionId = positionId;
        }


	public java.lang.Integer getPositionId()
        {
             return positionId;
        }


        public void setPositionName(java.lang.String positionName)
        {
             this.positionName = positionName;
        }


	public java.lang.String getPositionName()
        {
             return positionName;
        }


    }

