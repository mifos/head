alter table loan_offering
    add cashflow_comparison_flag smallint default 0,
    add cashflow_threshold decimal(5, 2);